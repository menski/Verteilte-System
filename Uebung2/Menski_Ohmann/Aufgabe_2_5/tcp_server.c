/*
 * TCP Server
 *
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <err.h>

#include "random.h"

#define RAND_LIMIT 10000000

int *
getrandomvalue_1_svc(messagecontent *msg, struct svc_req *rqstp)
{
	static int result;

	printf("server received %d %06d\n", msg->messagetype, msg->matrikelnumber);

	/* test message */
	if (msg->messagetype != 1 || msg->matrikelnumber <= 0) {
		warnx("receive message failed");
		return NULL;
	}
		
	/* set random number */
	srand(time(NULL));
	result = rand() % RAND_LIMIT;

	return &result;
}

int *
setmanipulatedvalue_1_svc(messagecontent *msg, struct svc_req *rqstp)
{
	static int result;
	
	printf("server received %d %06d %07d\n", msg->messagetype, msg->matrikelnumber, msg->randomvalue);

	/* test message */
	if (msg->messagetype != 3 || msg->matrikelnumber <= 0 || msg->randomvalue <= 0) {
		warnx("receive message failed");
		return NULL;
	}

	result = 0;

	return &result;
}
