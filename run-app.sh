#!/bin/bash

ASM_HOME=$HOME/Downloads/asm-master
ASM_JARS=$ASM_HOME/asm/build/libs/asm-9.10-SNAPSHOT.jar:$ASM_HOME/asm-analysis/build/libs/asm-analysis-9.10-SNAPSHOT.jar:$ASM_HOME/asm-commons/build/libs/asm-commons-9.10-SNAPSHOT.jar:$ASM_HOME/asm-tree/build/libs/asm-tree-9.10-SNAPSHOT.jar:$ASM_HOME/asm-util/build/libs/asm-util-9.10-SNAPSHOT.jar



cd bin
java  -cp $ASM_JARS:. $@
cd -
echo "---------------"
