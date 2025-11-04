#!/bin/bash

ASM_HOME=$HOME/Downloads/asm-master
ASM_JARS=$ASM_HOME/asm/build/libs/asm-9.10-SNAPSHOT.jar:$ASM_HOME/asm-analysis/build/libs/asm-analysis-9.10-SNAPSHOT.jar:$ASM_HOME/asm-commons/build/libs/asm-commons-9.10-SNAPSHOT.jar:$ASM_HOME/asm-tree/build/libs/asm-tree-9.10-SNAPSHOT.jar:$ASM_HOME/asm-util/build/libs/asm-util-9.10-SNAPSHOT.jar
PROJECT_HOME=/home/vignol-bande/odb-dan

cd $PROJECT_HOME/bin
rm -rf out
mkdir out

# Compiler Server.java
javac -g -cp . -d . $PROJECT_HOME/src/app/Server.java

# Compiler Serv.java avec Jakarta Servlet API
javac -g -cp .:$PROJECT_HOME/lib/jakarta.servlet-api-6.0.0.jar -d . $PROJECT_HOME/src/app/Serv.java

# Copier uniquement les classes compilées en conservant l’arborescence
cp -r * out/

# Exécuter Parser6 pour chaque classe spécifiée
for class in "$@"; do
    java -cp $ASM_JARS:.:$PROJECT_HOME/lib/jakarta.servlet-api-6.0.0.jar pack.Parser6 $class
done

cd $PROJECT_HOME
echo "---------------"

