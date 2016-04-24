<?php
$url = "http://fang.ihuhai.cn:8983/solr/new_core/select";
$projectName = $_GET['s'];
$callback = $_GET['callback'];
$params = array(
    'q' => 'projectName:"' . $projectName . '"',
    'wt' => 'json',
);
$url = $url . "?" . http_build_query($params);
//初始化
$ch = curl_init();
//设置选项，包括URL
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
//curl_setopt($ch, CURLOPT_POST, 1 );
curl_setopt($ch, CURLOPT_HEADER, 0);
//curl_setopt ( $ch, CURLOPT_POSTFIELDS, $params);
//执行并获取HTML文档内容
$output = curl_exec($ch);
//释放curl句柄
curl_close($ch);
//打印获得的数据
$out = '';
if(empty($callback)){
    $out = $output;
}else{
    $out = $callback . '(' . $output . ')';
}
header("Content-Type: text/html; charset=UTF-8");
echo $out;

?>
