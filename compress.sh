#!/bin/bash

SRCFOLDER=Menski_Ohmann
MAILTXT=mail.txt

function build() {
	if [ -e $1/$SRCFOLDER ]; then
		echo "Build $1"
		echo "Clean sources"
		find $1/$SRCFOLDER -type f -name "Makefile" -execdir make clean \;
		echo "Compress $1"
		tar czvf $1/$SRCFOLDER.tar.gz -C $1 $SRCFOLDER
		echo "Create mail text for $1"
		echo "To: mario.frank@uni-potsdam.de" > $1/$MAILTXT
		echo "From: menski@uni-potsdam.de" >> $1/$MAILTXT
		echo "CC: ohmann@uni-potsdam.de" >> $1/$MAILTXT
		echo -e "Subject: VS_${1}_Menski_Ohmann\n\nHallo Mario,\n\nanbei die Quelltexte zu $1 von:" | sed 's/Uebung/Blatt/' >> $1/$MAILTXT
		echo -e "Sebastian Menski (734272)\nMartin Ohmann (734801)" >> $1/$MAILTXT
		echo -e "\nGrüße Sebastian" >> $1/$MAILTXT
	fi
}

function sendmail() {
	if [ -e $1/$MAILTXT -a -e $1/$SRCFOLDER.tar.gz ]; then
		echo -n "Send mail? "
		read answer
		case $answer in
			Y|y|J|j)
				mutt -H $1/$MAILTXT -a $1/$SRCFOLDER.tar.gz
				;;
		esac
	fi
}

if [ -z "$1" ]; then
	for d in Uebung*; do
		build $d
	done
else
	build $1
	sendmail $1
fi
