import os

fileList = os.listdir()

for item in fileList:
	os.rename(src=item, dst=("a" + item))

