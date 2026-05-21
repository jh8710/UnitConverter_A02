Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")

Push-Location $repoRoot
try {
    mvn "-Dtest=GoldenMasterApprovalTest" "-Dgolden.master.update=true" test
    if ($LASTEXITCODE -ne 0) {
        throw "Golden Master generation failed with exit code $LASTEXITCODE"
    }

    git add "src/test/resources/golden_master_expected.txt"
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to stage src/test/resources/golden_master_expected.txt"
    }

    Write-Host "Generated and staged src/test/resources/golden_master_expected.txt"
} finally {
    Pop-Location
}
