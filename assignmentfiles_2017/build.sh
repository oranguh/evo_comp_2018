#!/bin/bash
shopt -s nullglob

# don't delete these class files
blacklist=( "BentCigarFunction.class" "KatsuuraEvaluation.class" "SchaffersEvaluation.class" "SphereEvaluation.class" )

# remove class files before making new ones
oldClassFiles=(*.class)
for i in "${blacklist[@]}"; do
    oldClassFiles=(${oldClassFiles[@]//*$i*})
done
printf "\nRemoving following files:\n"
printf '\t%s\n' "${oldClassFiles[@]}"
rm -f "${oldClassFiles[@]}"


# compile classes
javaFiles=(code/*.java)
printf "\nCompiling following files:\n"
printf '\t%s\n' "${javaFiles[@]}"
javac -d . -cp contest.jar "${javaFiles[@]}"


# pack new class files
newClassFiles=(*.class)
for i in "${blacklist[@]}"; do
    newClassFiles=(${newClassFiles[@]//*$i*})
done
printf "\nPacking following files:\n"
printf '\t%s\n' "${newClassFiles[@]}"
jar cmf MainClass.txt submission.jar "${newClassFiles[@]}"