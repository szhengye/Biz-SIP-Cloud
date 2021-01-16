curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/sample1" -X POST --data '{"accountNo":"62001818","sex":"0","email":"123232@163.com","mobile":"18601872345"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/sample2" -X POST --data '{"accountNo":"003"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -X POST --data '{"serviceId":"/openapi/sample2","accountNo":"003"}' http://localhost:8080/client1|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/sample4" -X POST --data '{"accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/sample5" -X POST --data '{"accountName": "王五","balance": 500,"accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server4" -X POST --data '{"accountName": "王五","sex": "0","accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server4" -X POST --data '{"accountName": "王五","sex": "1","accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server4" -X POST --data '{"accountName": "王五","sex": "2","accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server5" -X POST --data '{"accountName": "王五","sex": "0","accountNo":"005"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server5" -X POST --data '{"accountName": "王五","sex": "0","accountNo":"005","balance":1000}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server5" -X POST --data '{"accountName": "王五","sex": "2","accountNo":"005","balance":1000}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server6" -X POST --data '{"accountName": "王五","balance": 500,"accountNo":"005","sex":"0"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/server7" -X POST --data '{"accountName": "王五","balance": 500,"accountNo":"005","sex":"0"}' http://localhost:8000/api|jq
echo '{"accountName": "王五","balance": 500,"accountNo":"xxx"}'|nc -l 10001 &
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/netty" -X POST --data '{"accountNo":"999"}' http://localhost:8000/api|jq
echo '{"serviceId":"/openapi/sample2","accountNo":"003"}'|nc localhost 10002
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/db1" -X POST --data '{"accountNo":"002"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/redis1" -X POST --data '{"accountNo":"002"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/openapi/safservice" -X POST --data '{"accountNo":"003"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:sample1.service1" -X POST --data '{"accountNo":"003"}' http://localhost:8000/api|jq
curl -H "Content-Type:application/json" -H "Biz-Service-Id:/sample2/service2" -X POST --data '{"accountNo":"003"}' http://localhost:8000/api|jq
