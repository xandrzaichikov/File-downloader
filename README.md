# File-downloader
Files download utility 

## Project  Build
gradlew build<br/>
Executable jar - ../build/libs/utility.jar

## Run
command line:<br />
java -jar utility.jar -n <number_of_threads> -l <download_speed> -o <output_folder> -f <file_with_links_to_download><br />
example:<br />
java -jar utility.jar -n 3 -l 200k -o D:/tmp/downloadtmp/ -f D:/resources/file_list.txt<br />

## Example of file with links 

https://{host1}/1b4ef3.jpg  test2.jpg <br />
https://{host2}/a9c0cd.png  test3.png <br />
https://{host3}/KevinBourrillion_AnOverviewOfGuavaGoogleCoreLibrariesForJava.pdf  test2.pdf <br />
