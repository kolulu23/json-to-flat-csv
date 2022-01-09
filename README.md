# json-to-flat-csv

Convert nested json into flat csv. Json file can be line separated or just a bulk

# Usage

This is a CLI application. To see how to use this application, run

```shell
java -jar json2csv.jar help
```

> Suppose you have built your project and target JAR is called `json2csv.jar`.

## Example

Convert a compressed(line separated) HTTP response json file into csv.

Input:

```txt
{"success":true,"code":200,"message":"查询成功","data":{"metricresult":[{"resultValue":29.71,"metricCode":"wyaujqnfiv","metricName":"B12_2转出账户过去24小时转出交易金额均值","metricType":"REALTIME","templateType":"AVG"},{"resultValue":null,"metricCode":"ikhsmigbga","metricName":"B12_1转出账户过去24小时转出交易金额标准差","metricType":"REALTIME","templateType":"STD"},{"resultValue":0,"metricCode":"ln5qz00kag","metricName":"B11转出过去24小时内转出交易最大连续递减次数","metricType":"REALTIME","templateType":"TREND"}],"jylsh":"2021101931408023","transdate":"2021-06-30","zchh":"7465223","zrzh":"95271321"}}
{"success":true,"code":200,"message":"查询成功","data":{"metricresult":[{"resultValue":29.71,"metricCode":"wyaujqnfiv","metricName":"B12_2转出账户过去24小时转出交易金额均值","metricType":"REALTIME","templateType":"AVG"},{"resultValue":10,"metricCode":"yoi78silk","metricName":"B1X转出过去24小时内转出交易最大连续递减次数","metricType":"REALTIME","templateType":"TREND"}],"jylsh":"2021101931408023","transdate":"2021-06-30","zchh":"7465223","zrzh":null}}
```

Run this command:

```shell
java -jar json2csv.jar convert-metric -v \
-i ../data/metric_result_short.txt \
-o ../data/result.csv \
-m wyaujqnfiv,ikhsmigbga,yoi78silk,ln5qz00ka
```

Output:

```csv
wyaujqnfiv,ikhsmigbga,yoi78silk,ln5qz00ka,info
29.71,,,,(wyaujqnfiv#B12_2转出账户过去24小时转出交易金额均值#REALTIME#AVG)(ikhsmigbga#B12_1转出账户过去24小时转出交易金额标准差#REALTIME#STD)
29.71,,10,,(wyaujqnfiv#B12_2转出账户过去24小时转出交易金额均值#REALTIME#AVG)(yoi78silk#B1X转出过去24小时内转出交易最大连续递减次数#REALTIME#TREND)
```

---

## Example

Convert flat json(uncompressed) into csv.

Input:

```json
[
  {
    "foo": "bar",
    "count": 1,
    "null": null,
    "number": 0.678,
    "bool": true
  },
  {
    "foo": "bar",
    "count": 2,
    "null": null,
    "number": 0.678,
    "bool": true
  },
  {
    "foo": "bar",
    "count": 3,
    "null": null,
    "number": 0.678,
    "bool": true
  }
]
```

```shell
java -jar json2csv.jar convert-flat \
-i D:\Workspace\Projects\JavaProjects\json-to-flat-csv\data\uncompressed.json \
-m STANDARD
```

Output:

```csv
foo,count,null,number,bool
bar,1,,0.678,true
bar,2,,0.678,true
bar,3,,0.678,true
```