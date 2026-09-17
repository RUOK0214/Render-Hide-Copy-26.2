# 배포 상태 및 검증 범위 — Render Hide 2.6.1

최종 문서 확인: **2026-09-17**

## 현재 릴리스 결정

**v2.6.1은 알려진 제한을 공개한 정식 릴리스로 게시되었습니다.**
alpha.27의 기능을 기준으로 버전을 확정하고 Mod Menu의 한국어/영어 설명과 Discord 링크를 반영했습니다.
정식 릴리스라는 표시는 모든 버그 해결이나 모든 모드·셰이더 조합의 안정성 검증 완료를 뜻하지 않습니다.
남은 문제는 아래에 공개하며 후속 버전에서 수정합니다.

- [v2.6.1 다운로드 및 한·영 설치 안내](https://github.com/RUOK0214/Render-Hide-Copy-26.2/releases/tag/v2.6.1)
- 설치 파일: `render-hide-26.2-2.6.1+26.2.jar`
- 지원 환경: Minecraft Java Edition **26.2**, Java **25**, Fabric Loader **0.19.3 이상**, Minecraft 26.2용 Fabric API **0.158.0+26.2 이상**
- Sodium은 선택 사항이며 호환 코드의 기준은 **mc26.2-0.9.1-fabric**입니다.
- 기존 `v2.6.0-first-complete` 체크포인트는 유지합니다.

## 확인 완료한 범위

- 저장소 공개 상태, 비로그인 GitHub API 접근, v2.6.1의 정식·최신 릴리스 게시 상태.
- [릴리스 커밋의 GitHub Actions 빌드 성공](https://github.com/RUOK0214/Render-Hide-Copy-26.2/actions/runs/35180156487).
- 릴리스 태그가 빌드 커밋 `ded42ec6ed62e60e59970a7f9a920905280a03fa`를 가리킴.
- 첨부 JAR의 등록 상태 및 GitHub 기록 SHA-256과 보관된 검사 JAR의 해시 일치.
- 검사 JAR의 압축 무결성, 내부 버전·의존성·클라이언트 전용 선언, 엔트리포인트·믹스인 클래스 포함 여부.
- MIT 라이선스 원문·제작자 표기·아이콘·한/영 Mod Menu 설명 및 Discord 링크 포함.

JAR SHA-256:
```text
6aabe076b21ec0fad5939f7045d2e1d61447eba581449901745a91d3af8c3483
```

## 실행 검증과의 구분

이번 배포 점검에서는 실제 Minecraft를 실행하지 않았습니다.
사용자가 제공한 게임 화면과 시연 영상은 사용한 환경에서의 관찰 자료이며, 아래의 전체 조합별 회귀 확인표를 통과한 기록은 아닙니다.
공개 첨부 파일의 재다운로드 시도는 연결 시간 초과로 완료하지 못했으므로, 비로그인 다운로드의 처음부터 끝까지의 검증 완료로 표기하지 않습니다.
코드·JAR 검사와 실제 게임 실행 검증을 구분하며, 미확인 항목은 통과로 간주하지 않습니다.

## alpha.27에서 보완하여 v2.6.1에 포함한 항목

- 영역 add/toggle 명령 인수를 quoted string으로 통일: 공백·한글로 변경한 영역의 toggle 지원.
- alpha.26 엔티티 ID 콜론 처리 수정 유지.
- 초기화 로그에 실제 모드 버전 표시.
- 차원 변경/월드 이탈 감지 시 작성 중인 두 선택 좌표 초기화.
- 설정 로드 시 null/누락/역전 좌표 영역 및 중복 이름 방어, null 필터 ID 건너뛰기.
- 불투명도의 NaN/Infinity 방어.
- 설정 저장 시 완성된 JSON을 임시 파일에서 교체. 지원 파일시스템에서 atomic move 사용.
  다중 파일 전체를 하나의 트랜잭션으로 저장하지는 않으며 손상 파일 자동 복구/백업 기능은 아니다.
- 슬라이더 숫자와 실제 5% 단위 적용값 일치.
- 화면 전환 시 이전 필터 입력 위젯 참조 초기화.
- 이동 블록 이웃 재조회 시 null/타입 재검사.
- 엔티티 상태 추출 null 방어 및 불투명도 0의 모델 제출 차단.
- 바이너리/소스 JAR에 MIT 원문 포함, 설치 아티팩트에서 sources JAR 제외.
- alpha.16에 머물던 README를 실제 기능·제약 기준으로 교체.

## 알려진 제한 및 후속 검증 과제

| 우선도 | 내용 | 근거/영향 |
| --- | --- | --- |
| 높음 | 월드/서버별 영역 분리 없음 | RegionManager는 차원만 비교. 다른 월드의 같은 좌표도 숨겨짐 |
| 높음 | 아이템 액자 본체 및 접촉면 | 기존 사용자 보고. 해결로 판정할 실행 근거 없음 |
| 높음 | Sodium 내부 API 버전 민감성 | optional @Pseudo 믹스인이 내부 클래스/람다 메서드에 주입. 다른 버전 호환 보장 불가 |
| 중간 | 엔티티 부가 표현 불투명도 누락 | collector의 name tag/flame/leash/particle 메서드는 그대로 위임 |
| 중간 | 무색 블록 모델 alpha 경로 | submitBlockModel은 tint 배열만 보정. untinted geometry의 alpha를 일반적으로 보장하지 못함 |
| 중간 | 가상 조명 성능/스레드 | chunk meshing 경로에서 live client.level을 조회하며 최대 512칸 탐색. 성능 및 동시 월드 변경 실험 필요 |
| 중간 | 이동면 제거 범위 | MovingBlockFeatureRenderer의 culling 인수가 항상 true. 비활성/100%/영역 밖 회귀 확인 필요 |
| 중간 | 작은 화면 GUI | 고정 y좌표 사용. 하단 버튼 clipping 가능, 반응형 배치 필요 |
| 중간 | 설정 저장 오류 전달 | 디스크 실패는 로그에 기록되지만 메모리 변경/성공 메시지는 유지될 수 있음 |
| 낮음 | 전역/조명 스위치 미저장 | 다시 실행하면 ON. 사용자 문서에 명시 |
| 낮음 | 지역명 생성/변경 차이 | 생성은 영문 정규화, 이름 변경은 한글/공백 지원 |
| 낮음 | 번역 범위 | 단축키는 한/영 번역, 설정 UI·메시지는 대부분 영어 |

## 후속 실제 게임 회귀 확인표

각 항목을 **Fabric API만 / Sodium 0.9.1 / Sodium 0.9.1 + Iris(셰이더 OFF/ON)**에서 확인한다.
Iris 버전, 셰이더 이름·버전, GPU, 리소스팩을 기록한다.
체크되지 않은 항목은 통과로 간주하지 않는다.

- [ ] 시작 → 타이틀 → 월드 입장 → 재접속, 로그에 Mixin 오류 없음
- [ ] 불투명도 0/5/50/95/100, 전체 OFF, 영역 OFF에서 즉시 정상 복원
- [ ] 필터 블록/비필터 블록/인접 영역/중첩 영역 경계
- [ ] 피스톤 밀기·당기기·슬라임·꿀, 이동 중 영역 진입/이탈, OFF와 100% 회귀
- [ ] 물·용암·수중·물먹은 블록, 유리·나뭇잎·반블록
- [ ] 상자·구리상자·셜커상자·종·장식된 도자기, 애니메이션 및 원거리
- [ ] 액자(빈/아이템/지도/발광), display 3종, 몹·보트·광산수레·드롭 아이템
- [ ] 밤/동굴/네더의 버추얼 라이트 ON/OFF와 필터 블록 방향별 음영
- [ ] e_filter add/remove minecraft:allay, 자동완성, 잘못된 ID, 중복 추가
- [ ] 한글·공백 이름 변경 후 toggle/remove, 필터 유지, 재실행 후 저장 유지
- [ ] 작은 창/높은 GUI 배율/여러 페이지/필터 복사·붙여넣기
- [ ] 월드 A→B 및 차원 이동: 공유 설정 의도 확인, 선택 좌표 초기화
- [ ] 큰 영역·여러 영역에서 슬라이더 변경 및 청크 갱신 성능

## 배포 및 오류 제보 안내

v2.6.1은 위 제한을 공개한 정식 릴리스로 배포합니다. alpha 사전 배포 후보였던 이전 문서의 판정을 현재 릴리스 상태와 구분하기 위해 이 문서를 갱신했습니다.
이번 안내 갱신은 문서 수정이며, v2.6.1 JAR과 릴리스 태그 및 기존 first-complete 태그는 변경하지 않습니다.
태그에 고정된 소스에는 당시 문서가 보존되므로, 최신 검증 안내는 [main의 이 문서](https://github.com/RUOK0214/Render-Hide-Copy-26.2/blob/main/docs/RELEASE_READINESS.md)를 참고하세요.

오류 제보 시 모드 버전, Minecraft/Fabric/Sodium/Iris 버전, 셰이더·리소스팩, GPU, 재현 과정, 불투명도·필터 설정, `latest.log` 또는 충돌 보고서를 함께 제공하세요.
[GitHub Issues](https://github.com/RUOK0214/Render-Hide-Copy-26.2/issues) 또는 [Discord](https://discord.gg/95secUdgMK)에서 제보할 수 있습니다.

## English summary

v2.6.1 is published as a regular release with known limitations disclosed. It is based on alpha.27 and includes localized Mod Menu descriptions and a Discord link.
Publication does not mean all bugs or all renderer/shader combinations have been validated.
The release build, archive contents, metadata and matching SHA-256 have been checked; this audit did not launch Minecraft or complete the runtime checklist above.
Item frames, touching faces, shared region settings across worlds, and renderer compatibility remain documented limitations or follow-up work.
See the [release page](https://github.com/RUOK0214/Render-Hide-Copy-26.2/releases/tag/v2.6.1) for English installation instructions, controls and known limitations.
