"""Exercise the production cache with minimal Minecraft API stubs (JDK 17+)."""
from pathlib import Path
import subprocess
import tempfile

STUBS = {
    "net/minecraft/client/Minecraft.java": """package net.minecraft.client;
public class Minecraft {
 private static final Minecraft INSTANCE = new Minecraft();
 private final Thread owner = Thread.currentThread();
 public static Minecraft getInstance() { return INSTANCE; }
 public boolean isSameThread() { return Thread.currentThread() == owner; }
}""",
    "net/minecraft/core/BlockPos.java": """package net.minecraft.core;
public record BlockPos(int x, int y, int z) {
 public BlockPos immutable() { return this; }
 public int getX() { return x; }
 public int getZ() { return z; }
}""",
    "net/minecraft/world/level/Level.java": "package net.minecraft.world.level; public class Level {}",
    "net/minecraft/client/multiplayer/ClientLevel.java": "package net.minecraft.client.multiplayer; public class ClientLevel extends net.minecraft.world.level.Level {}",
    "net/minecraft/world/level/ChunkPos.java": """package net.minecraft.world.level;
public record ChunkPos(int x, int z) {}""",
    "net/minecraft/world/level/block/state/BlockState.java": "package net.minecraft.world.level.block.state; public record BlockState(int id) {}",
    "net/minecraft/world/level/block/entity/BlockEntity.java": """package net.minecraft.world.level.block.entity;
import net.minecraft.core.BlockPos;
public class BlockEntity { private final BlockPos pos;
 public BlockEntity(BlockPos pos) {this.pos=pos;} public BlockPos getBlockPos() {return pos;} }""",
    "net/minecraft/world/level/block/piston/PistonMovingBlockEntity.java": """package net.minecraft.world.level.block.piston;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public class PistonMovingBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity {
 private final BlockState moved;
 public PistonMovingBlockEntity(BlockPos pos, BlockState moved) {super(pos);this.moved=moved;}
 public BlockState getMovedState() {return moved;} }""",
    "CacheTest.java": """import com.ruok0214.renderhide.MovingPistonStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import java.util.concurrent.atomic.AtomicReference;
public class CacheTest {
 static void check(boolean value) {if (!value) throw new AssertionError();}
 public static void main(String[] args) throws Exception {
  Minecraft.getInstance();
  var a=new ClientLevel(); var b=new ClientLevel();
  var p=new BlockPos(-1,64,-17); var q=new BlockPos(32,64,0);
  var state=new BlockState(1);
  var piston=new PistonMovingBlockEntity(p,state);
  MovingPistonStates.put(a,piston);
  check(MovingPistonStates.get(a,p)==state);
  check(MovingPistonStates.get(b,p)==null);
  MovingPistonStates.put(a,new BlockEntity(p));
  check(MovingPistonStates.get(a,p)==null);
  MovingPistonStates.put(a,piston);
  MovingPistonStates.put(a,new PistonMovingBlockEntity(q,state));
  MovingPistonStates.removeChunk(a,new ChunkPos(-1,-2));
  check(MovingPistonStates.get(a,p)==null);
  check(MovingPistonStates.get(a,q)==state);
  MovingPistonStates.useLevel(b);
  check(MovingPistonStates.get(a,q)==null);
  MovingPistonStates.put(b,piston);
  MovingPistonStates.remove(a,p);
  check(MovingPistonStates.get(b,p)==state);
  AtomicReference<Throwable> failure=new AtomicReference<>();
  Thread worker=new Thread(() -> {
   try {
    MovingPistonStates.put(a,piston); // Off-thread writes must be ignored.
    MovingPistonStates.useLevel(a);
    for(int i=0;i<250000;i++) {
     var found=MovingPistonStates.get(b,p);
     check(found==null || found==state);
    }
   } catch(Throwable t) {failure.set(t);}
  });
  worker.start();
  for(int i=0;i<50000;i++) {
   MovingPistonStates.put(b,piston);
   MovingPistonStates.remove(b,p);
  }
  worker.join();
  if(failure.get()!=null) throw new AssertionError(failure.get());
  MovingPistonStates.put(b,piston);
  check(MovingPistonStates.get(b,p)==state);
  MovingPistonStates.useLevel(null);
  check(MovingPistonStates.get(b,p)==null);
  check(MovingPistonStates.get(null,p)==null);
  System.out.println("PASS: publication, replacement, negative chunk unload, world isolation, disconnect, concurrent reads");
 }
}""",
}

root = Path(__file__).resolve().parents[1]
production = root / "src/client/java/com/ruok0214/renderhide/MovingPistonStates.java"
with tempfile.TemporaryDirectory() as tmp:
    tmp = Path(tmp)
    files = []
    for name, content in STUBS.items():
        path = tmp / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content)
        files.append(str(path))
    subprocess.run(["javac", "-d", str(tmp), str(production), *files], check=True)
    subprocess.run(["java", "-cp", str(tmp), "CacheTest"], check=True)
