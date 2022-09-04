#!/bin/bash
token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2hhaWwuZHZvcmtpbkBnbWFpbC5jb20iLCJleHAiOjE2NjIyOTQzOTEsIm9yaWdfaWF0IjoxNjYyMjA3OTkxfQ.XQvEEm_DwOiO8dzU00LCUOLJjTDjtBRBACeCVGQUZqM
cd ../output/
rm best.txt
for i in {01..35} ; do
	name=`ls -1 ${i}_?????_* | head -n 1`
	echo $name >> best.txt
	if [ -n "$1" ]; then echo curl --header "Authorization: Bearer $token" -F file=@$name https://robovinci.xyz/api/submissions/$i/create; fi;
done
