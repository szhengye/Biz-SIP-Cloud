echo "build api-gateway..."
cp api-gateway/src/main/docker/Dockerfile api-gateway/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-api-gateway api-gateway/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-api-gateway
echo "build integrator..."
cp integrator/src/main/docker/Dockerfile integrator/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-integrator integrator/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-integrator
echo "build dynamic-config..."
cp dynamic-config/src/main/docker/Dockerfile dynamic-config/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-dynamic-config dynamic-config/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-dynamic-config
echo "build sample-client..."
cp sample/sample-client/src/main/docker/Dockerfile sample/sample-client/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-sample-client sample/sample-client/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-sample-client
echo "build sample-server..."
cp sample/sample-server/src/main/docker/Dockerfile sample/sample-server/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-sample-server sample/sample-server/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-sample-server
echo "build netty-client-adaptor..."
cp client-adaptor/netty-client-adaptor/src/main/docker/Dockerfile client-adaptor/netty-client-adaptor/target
docker build -t registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-netty-client-adaptor client-adaptor/netty-client-adaptor/target
docker push registry.cn-shanghai.aliyuncs.com/szhengye/bizsip:bizsip-netty-client-adaptor
