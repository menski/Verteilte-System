/*
 * TCP Client
 *
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */

#include <sysexits.h>
#include <err.h>

#include "random.h"

#define MATNR_MIN 1
#define MATNR_MAX 999999

void
client(char *host, int mat_nr)
{
	CLIENT *clnt;
	int  *result;
	messagecontent  msg;

#ifndef	DEBUG
	clnt = clnt_create (host, RANDOM_PROG, RANDOM_VERS, "udp");
	if (clnt == NULL) {
		clnt_pcreateerror(host);
		exit (1);
	}
#endif	/* DEBUG */

	/* prepare first message */
	msg.messagetype = 1;
	msg.matrikelnumber = mat_nr;
	msg.randomvalue = 0;

	/* receive random number */
	result = getrandomvalue_1(&msg, clnt);
	if (result == (int *) NULL) {
		clnt_perror(clnt, "call failed");
	}
	printf("client received %07d\n", *result);

	/* test result */
	if (*result <= 0)
		errx(EX_DATAERR, "Receive random failed");
	
	/* increase messagetype */
	msg.messagetype = 3;
	
	/* set random value */
	msg.randomvalue = *result;

	/* send third message */
	result = setmanipulatedvalue_1(&msg, clnt);
	if (result == (int *) NULL) {
		clnt_perror(clnt, "call failed");
	}

	/* test result */
	if (*result != 0)
		errx(EX_DATAERR, "Set value failed");

#ifndef	DEBUG
	clnt_destroy (clnt);
#endif	 /* DEBUG */
}


int
main (int argc, char *argv[])
{
	char *host;
	int mat_nr;
	
	/* parse arguments */
	if (argc < 3) {
		errx(EX_USAGE, "Server address and Mat-NR. required\nusage: %s SERVER_ADDR MAT_NR", argv[0]);
	}
	host = argv[1];
	mat_nr = atoi(argv[2]);
	if (mat_nr < MATNR_MIN || mat_nr > MATNR_MAX) {
		errx(EX_USAGE, "MAT_NR must be between %d and %d\nusage: tcp_client SERVER_ADDR MAT_NR [PORT]", MATNR_MIN, MATNR_MAX);
	}

	/* start client */
	client(host, mat_nr);

	return EX_OK;
}
