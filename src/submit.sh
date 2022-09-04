#!/bin/bash
token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2hhaWwuZHZvcmtpbkBnbWFpbC5jb20iLCJleHAiOjE2NjIzODExMTYsIm9yaWdfaWF0IjoxNjYyMjk0NzE2fQ.T5NGMuOo49_jkLnooVkfNXZ6P1RFE9jKDPJIoznddKM
cd ../output/
rm best.txt
for i in {01..40} ; do
	name=`ls -1 ${i}_?????_* | head -n 1`
	echo $name >> best.txt
	if [ -n "$1" ]; then curl --header "Authorization: Bearer $token" -F file=@$name https://robovinci.xyz/api/submissions/$i/create; fi;
done
