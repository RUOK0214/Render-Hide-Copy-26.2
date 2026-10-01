# 2.6.2: moving-piston lookup during chunk meshing

The reported September 27 crash reached `LevelChunk.getBlockEntity` from
`RegionManager.isMovingPushedBlockVisible` on a Sodium chunk-meshing worker.
The failing fastutil map lookup is consistent with concurrent access to the
live chunk block-entity map. It is not evidence of an oak-trapdoor model bug.

The filter lookup now reads immutable `BlockState` values from a concurrent
registry. The client chunk publishes the moved state after `setBlockEntity`,
and removes it before `removeBlockEntity`. Chunk unload, disconnect and world
replacement discard cached entries. The registry checks world identity rather
than just dimension or coordinates. Integrated-server chunks cannot populate it.
No live block entity is returned to a render worker. No exceptions are swallowed.

The patch preserves filter matching and opacity rules. It does not fix item
frames or contact-face artifacts, and it does not replace all virtual-light
block/light sampling with a complete world snapshot.

## Checks

`python3 scripts/test_moving_piston_states.py` exercises the production registry
against minimal API stubs: replacement, negative-coordinate chunk unload, world
isolation, disconnect, rejected off-thread publication, and concurrent reads
while entries are added/removed. This is not an in-game test or API compatibility
check; the normal Gradle build checks compilation against Minecraft/Fabric.

In-game verification still required: run a repeating piston circuit near a
hidden region boundary with Virtual Light enabled, both with Sodium 0.9.1 and
without Sodium; change filters/opacity; unload/reload the chunk; reconnect to a
second world in the same dimension. Confirm moved filtered blocks remain visible
and the reported chunk-meshing crash does not recur.
