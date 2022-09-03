cd ../output/
for i in {1..25} ; do
	curl --header "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2hhaWwuZHZvcmtpbkBnbWFpbC5jb20iLCJleHAiOjE2NjIyMTQwODEsIm9yaWdfaWF0IjoxNjYyMTI3NjgxfQ.euR3KZDg7mnXoe6R-Cj9JYBwfN7bzgEZsSU0r1NQ8vo" -F file=@$i.out https://robovinci.xyz/api/submissions/$i/create;
done
