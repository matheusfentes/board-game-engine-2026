# board-game-engine-2026

Reusable Java engine structure for running board games from JSON configuration.
The current runnable game is Xiangqi, configured in `games/xiangqi.json`.
`games/trilha.json` is retained as a data example; its runtime is not implemented yet.

## Build and run without Maven

Maven is optional. Gson is the only external library needed at runtime. From
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

On macOS/Linux, replace `;` with `:` in the Java classpath. If no config path
is provided, `Main` loads `games/xiangqi.json` by default. The Maven dependency
is also declared in `pom.xml` for environments where Maven is available.
