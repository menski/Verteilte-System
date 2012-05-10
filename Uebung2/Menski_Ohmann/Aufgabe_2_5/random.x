struct messagecontent { 
	int messagetype;
	int matrikelnumber; 
	int randomvalue; 
}; 

program RANDOM_PROG { 
	version RANDOM_VERS { 
		int GETRANDOMVALUE(messagecontent) = 1; 
		int SETMANIPULATEDVALUE(messagecontent) = 2; 
	} = 1; 
} = 0x23452222;