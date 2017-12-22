# File-downloader
Files download utility 

## Project  Build
gradlew build
Build zip file in build/distributions/filedownloader.zip (and .tar)
Unzip file will produce
├── filedownloader
    ├── lib
    ├── utility.jar 


## Run
Change dir to filedownloader.
command line:
java -jar utility.jar -n <number_of_threads> -l <download_speed> -o <output_folder> -f <file_with_links_to_download>
example:
java -jar utils.jar -n 3 -l 200k -o D:/tmp/downloadtmp/ -f D:/resources/file_list.txt

## Example of file with links 
https://{host1}/1b4ef3.jpg  test2.jpg
https://{host2}/a9c0cd.png  test3.png
https://{host3}/KevinBourrillion_AnOverviewOfGuavaGoogleCoreLibrariesForJava.pdf  test2.pdf
