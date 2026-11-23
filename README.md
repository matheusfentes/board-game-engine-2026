# board-game-engine-2026

Reusable Java engine structure for running board games from JSON configuration.
Xiangqi and Trilha are runnable from `games/xiangqi.json` and `games/trilha.json`.

## Build and run without Maven

Gson is the only external library needed at runtime. From
the project root in PowerShell, download its JAR once:

```powershell
New-Item -ItemType Directory -Force lib | Out-Null
Invoke-WebRequest -Uri "https://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar" -OutFile "lib/gson-2.11.0.jar"
```

Compile all Java sources and run Xiangqi:

```powershell
$sources = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "lib\gson-2.11.0.jar" -d out $sources
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
chcp 65001
java -cp "out;lib\gson-2.11.0.jar" Main games/xiangqi.json
```

To start Trilha, use `games/trilha.json` as the final argument instead.

On macOS/Linux, replace `;` with `:` in the Java classpath. If no config path
is provided, `Main` loads `games/xiangqi.json` by default. The Maven dependency
is also declared in `pom.xml` for environments where Maven is available.

## AI Usage Disclosure

Generative AI tools were used to support the development of this project, including
clarifying concepts, exploring possible solutions, and reviewing code.
The team reviewed and adapted the suggestions and is responsible for the final
content and behavior of the project.