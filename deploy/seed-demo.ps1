[CmdletBinding()]
param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'
$baseUrl = "http://127.0.0.1:$Port"
$title = '本地演示秒杀券'
$voucherList = $null
for ($attempt = 1; $attempt -le 15; $attempt++) {
    try {
        $voucherList = Invoke-RestMethod "$baseUrl/api/voucher/list/1" -TimeoutSec 2
        if ($voucherList.success) {
            break
        }
    } catch {
        $voucherList = $null
    }

    if ($attempt -lt 15) {
        Start-Sleep -Seconds 1
    }
}
if (-not $voucherList.success) {
    throw "本地店铺接口尚不可用。请确认 Docker Compose 服务已启动，然后重试。"
}

$now = [DateTimeOffset]::UtcNow
$nowMillis = $now.ToUnixTimeMilliseconds()
$current = $voucherList.data | Where-Object {
    $_.title -eq $title -and $_.type -eq 1 -and
        $_.beginTimeMillis -le $nowMillis -and $_.endTimeMillis -gt $nowMillis
} | Select-Object -First 1
if ($current) {
    Write-Output "103 茶餐厅已有演示秒杀券（编号 $($current.id)，当前库存 $($current.stock) 张）。"
    return
}

$voucher = @{
    shopId = 1
    title = $title
    subTitle = '每个账号限抢 1 张'
    rules = '仅供本机演示，订单不会扣款。'
    payValue = 100
    actualValue = 1000
    type = 1
    status = 1
    stock = 30
    beginTime = $now.AddMinutes(-5).ToString('yyyy-MM-ddTHH:mm:ss')
    endTime = $now.AddDays(7).ToString('yyyy-MM-ddTHH:mm:ss')
}
$created = Invoke-RestMethod "$baseUrl/api/voucher/seckill" -Method Post `
    -ContentType 'application/json; charset=utf-8' -Body ($voucher | ConvertTo-Json -Compress) -TimeoutSec 10
if (-not $created.success -or -not $created.data) {
    throw "未能创建演示券。请查看后端日志后重试。"
}
Write-Output "已为 103 茶餐厅添加演示秒杀券（编号 $($created.data)，库存 $($voucher.stock) 张，有效期约 7 天）。"
Write-Output "$baseUrl/shop-detail.html?id=1#vouchers"
