#!/bin/bash

readonly JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
readonly version=0.0.1
readonly dirSrc=$(pwd)/src
readonly dirBin=$(pwd)/bin
readonly dirTarget=$(pwd)/target
readonly filename=gews_${version}.jar
readonly fileJar=${dirTarget}/${filename}
readonly fileNative=${dirTarget}/gews
readonly fileManifest=${dirTarget}/manifest.txt
readonly fileDockerfile=${dirTarget}/Dockerfile
readonly classMain="gews.Main"

function printHelp {
	cat <<EOF
Simple build script to compile, package and create native image of the
application.

	./build.sh [all] [clean] [compile] [package] [native]
	
EOF
}

function initTools {
	echo "Initialize tooling."
	PATH=${JAVA_HOME}/bin:${PATH}
	export PATH
}

function clean {
	echo "Cleaning binary folder."
	rm -rf ${dirBin}/*
	
	echo "Cleaning target folder."
	rm -rf ${dirTarget}/*
}

function compile {
	echo "Compiling project."
	javac -d bin $(find src -type f -name '*.java' | tr '\n' ' ')
}

function package {
	echo "Packaging project."
	
	echo "Generating manifest file."
	cat > ${fileManifest} <<EOF
Manifest-Version: 1.0
Main-Class: ${classMain}
EOF
	
	echo "Creating JAR files ${fileJar}"
	jar cfm ${fileJar} ${fileManifest} -C ${dirBin} .
}

function native {
	echo "Creating native runnable."
	
	echo "Create GraalVM docker image."
	cat > ${fileDockerfile} <<EOF
FROM ghcr.io/graalvm/graalvm-ce:latest
WORKDIR /opt/graalvm
RUN gu install native-image
ENTRYPOINT ["native-image"]
	
EOF

	currentDir=$(pwd)
	cd ${dirTarget}
	docker build -t graalvm-native-image .
	cd ${currentDir}

	echo "Run GraalVM Docker image to compile native image."	
	docker run --rm -it -v ${dirTarget}:/opt/cp -v ${dirTarget}:/opt/graalvm graalvm-native-image "--static" "-H:Name=out" "-H:+ReportExceptionStackTraces" "-jar" "/opt/cp/${filename}" "${classMain}"
}

function main {
	echo "Main"
	initTools
	
	POSITIONAL_ARGS=()
	while [[ $# -gt 0 ]]; do
		case $1 in
			clean)
				clean
				shift
				;;
			compile)
				compile
				shift
				;;
			package)
				package
				shift
				;;
			native)
				native
				shift
				;;
			all)
				clean
				compile
				package
				native
				shift
				;;
			-h)
				printHelp
				shift
				;;
			-*|--*)
				echo "Unknown option $1"
				exit 1
				;;
			*)
				POSITIONAL_ARGS+=("$1") # save positional arg
				shift # past argument
				;;
		esac
	done
}

main $@
