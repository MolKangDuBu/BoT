
set -e

docker stop $(docker ps -a -q)
docker rm $(docker pa -a -q)
docker rmi -f $(docker images dev-* -q)