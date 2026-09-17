# Render Hide — Minecraft 26.2 (Fabric)

지정한 직육면체 영역의 블록·엔티티를 클라이언트 화면에서 숨기거나 반투명하게 표시합니다.
월드의 블록, 충돌, 레드스톤 동작을 변경하지 않습니다.

**현재 버전: 2.6.1+26.2 · 알려진 제한을 포함한 릴리스**
아이템 액자 본체와 반투명 접촉면에 알려진 문제가 남아 있습니다.
이번 릴리스는 alpha.27의 기능을 기준으로 확정했으며, 남은 문제는 이후 버전에서 수정합니다. 모든 셰이더 호환을 보장하지 않습니다.
상세 점검 결과와 게임 내 확인 항목: [배포 점검](docs/RELEASE_READINESS.md).

## 설치

- Minecraft Java Edition **26.2**, Java **25**
- Fabric Loader **0.19.3 이상**
- Minecraft 26.2용 Fabric API **0.158.0+26.2 이상**
- Sodium은 선택 사항입니다. 현재 호환 코드의 기준은 **mc26.2-0.9.1-fabric**입니다.
- Iris 및 개별 셰이더의 호환성은 실제 사용 조합별로 확인해야 합니다.

`render-hide-26.2-2.6.1+26.2.jar`를 클라이언트 `mods` 폴더에 넣으세요.
기존 Render Hide JAR은 제거하세요. `-sources.jar`는 설치 파일이 아닙니다.
서버에는 설치할 필요가 없습니다.

## 사용

| 기본 키 | 기능 |
| --- | --- |
| H | 설정 창 |
| F9 / F10 | 바라보는 블록을 영역의 첫째 / 둘째 모서리로 지정 |
| F7 | 선택 영역 저장 |
| F8 | 전체 숨김 ON/OFF |
| F6 | 저장된 영역 테두리 ON/OFF |

설정 창에서 영역별 ON/OFF, 이름·좌표 변경, 전역·영역별 블록/엔티티 필터를 관리합니다.
필터에 추가한 종류는 **숨김 예외**가 되어 정상 표시됩니다.
불투명도는 0~100%의 **5% 단위**이며 블록과 엔티티에 함께 적용됩니다.
버추얼 라이트는 화면의 조명 보정이며 실제 월드 밝기를 바꾸지 않습니다.

```mcfunction
/renderhide e_filter add minecraft:allay
/renderhide e_filter remove minecraft:allay
/renderhide e_filter list
/renderhide filter add minecraft:redstone_block
/renderhide filter remove minecraft:redstone_block
/renderhide toggle "테스트 영역"
/renderhide global
/renderhide light
/renderhide gui
```

`/renderhide e_filter add`만 입력하면 바라보는 엔티티의 종류를 추가합니다.
`/renderhide reload`는 렌더를 재구축하며 설정 파일을 다시 읽는 명령이 아닙니다.
영역 생성 이름은 기존 영문 식별자 방식으로 정규화됩니다. 한글·공백 이름은 영역 편집 창에서 변경할 수 있습니다.

## 설정 저장 범위

`config/renderhide-*.json`에 영역·필터·불투명도가 저장됩니다.
**월드/서버별 분리가 없습니다. 같은 차원의 같은 좌표에는 다른 월드에서도 저장된 영역이 적용됩니다.**
필요하면 F8로 숨김을 끄세요. 전역 ON/OFF와 버추얼 라이트 ON/OFF는 재실행 시 기본값 ON으로 돌아갑니다.
백업할 때는 게임을 종료하고 해당 JSON 파일을 모두 복사하세요.
영역 이름 변경은 여러 설정 파일에 걸쳐 저장되므로 파일 묶음 전체를 백업해야 합니다.

## 알려진 제한

- 아이템 액자 본체, 필터 경계와 이동 블록 접촉면은 추가 검증/수정이 필요합니다.
- 엔티티 이름표·불꽃·목줄·일부 파티클은 일반 모델과 같은 반투명 처리가 적용되지 않습니다.
- 반투명 아이템은 인챈트 광택이 생략됩니다.
- 블록 엔티티의 원래 표시 거리는 늘어나지 않습니다.
- GUI가 작으면 아래쪽 버튼이 화면 밖으로 나갈 수 있습니다. GUI 배율을 낮춰 사용하세요.
- 큰 영역에서 불투명도 변경과 버추얼 라이트는 렌더 재구축 비용이 발생합니다.

## 빌드 및 라이선스

Java 25에서 `./gradlew build`를 실행하세요. 결과는 `build/libs`에 생성됩니다.
GitHub Actions의 설치용 아티팩트에는 바이너리 JAR만 포함됩니다.

MIT License, Copyright (c) 2026 RUOK0214. [LICENSE](LICENSE).
기존 저장 지점 `v2.6.0-first-complete`는 이 버전과 별개이며 유지됩니다.

## 문의 및 커뮤니티

[괜찮으신가요 디스코드](https://discord.gg/95secUdgMK)

모드 메뉴 설명은 게임 언어에 따라 한국어/영어로 표시됩니다. 링크 목록의 Discord에서 커뮤니티에 접속할 수 있습니다.
