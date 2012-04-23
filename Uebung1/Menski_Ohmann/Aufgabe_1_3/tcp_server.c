/**
 * TCP Server
 * 
 * @author Sebastian Menski (734272) <menski@uni-potsdam.de>
 * @author Martin Ohmann (734801) <ohmann@uni-potsdam.de>
 */
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <sysexits.h>
#include <time.h>
#include <err.h>

#define ADDR "127.0.0.1"
#define PORT 10041
#define BUF_SIZE 1024
#define MAX_QUEUE 1
#define RAND_LIMIT 10000000

enum { ERROR = -1, SUCCESS };

int main(int argc, char *argv[]) {
	int server_sockfd, client_sockfd, result;
	int port, msg_id, mat_nr, rand_nr;
	struct sockaddr_in server_address, client_address;
	socklen_t client_len;
	char *addr_string, buf[BUF_SIZE];

	/* parse arguments */
	addr_string = argc > 1 ? argv[1] : ADDR;
	port = argc > 2 ? atoi(argv[2]) : PORT;

	/* set random number */
	srand(time(NULL));
	rand_nr = rand() % RAND_LIMIT;
	
	/* create server socket */
	server_sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (server_sockfd == ERROR) {
		err(EX_OSERR, "unable to create socket");
	}

	/* set server address values */
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr= inet_addr(addr_string);
	server_address.sin_port = port;

	/* bind socket to address */
	result = bind(server_sockfd, (struct sockaddr *) &server_address, sizeof(server_address));
	if (result == ERROR) {
		close(server_sockfd);
		err(EX_OSERR, "unable to bind to address %s:%d", addr_string, port);
	}

	/* listen on socket */
	result = listen(server_sockfd, MAX_QUEUE);
	if (result == ERROR) {
		close(server_sockfd);
		err(EX_OSERR, "unable to listen");
	}

	printf("%s: server waiting\n", argv[0]);

	/* accept client */
	client_len = sizeof(client_address);
	client_sockfd = accept(server_sockfd, (struct sockaddr *) &client_address, &client_len);
	if (client_sockfd == ERROR) {
		close(server_sockfd);
		err(EX_OSERR, "unable to accept client");
	}

	/* read first message */
	result = read(client_sockfd, buf, BUF_SIZE);
	if (result == ERROR) {
		close(client_sockfd);
		close(server_sockfd);
		err(EX_OSERR, "unable to read message from client");
	}
	printf("%s: received \"%s\"\n", argv[0], buf);
	result = sscanf(buf, "%d %d", &msg_id, &mat_nr);
	if (result != 2) {
		close(client_sockfd);
		close(server_sockfd);
		errx(EX_DATAERR, "parsing error");
	}

	if (msg_id != 1) {
		close(client_sockfd);
		close(server_sockfd);
		errx(EX_DATAERR, "wrong message id from client");
	}

	/* send answer to client */
	result = sprintf(buf, "%d %06d %07d", 2, mat_nr, rand_nr);
	if (result < 0) {
		close(client_sockfd);
		close(server_sockfd);
		errx(EX_DATAERR, "unable to format answer");
	}
	result = write(client_sockfd, buf, result + 1); /* +1 for \0 string terminator */
	if (result == ERROR) {
		close(client_sockfd);
		close(server_sockfd);
		err(EX_OSERR, "unable to send message");
	}

	/* read second message */
	result = read(client_sockfd, buf, BUF_SIZE);
	if (result == ERROR) {
		close(client_sockfd);
		close(server_sockfd);
		err(EX_OSERR, "unable to read message from client");
	}
	printf("%s: received \"%s\"\n", argv[0], buf);
	result = sscanf(buf, "%d %d %d", &msg_id, &mat_nr, &rand_nr);
	if (result != 3) {
		close(client_sockfd);
		close(server_sockfd);
		errx(EX_DATAERR, "parsing error");
	}
	if (msg_id != 3) {
		close(client_sockfd);
		close(server_sockfd);
		errx(EX_DATAERR, "wrong message id from client");
	}

	/* close sockets and return */
	close(client_sockfd);
	close(server_sockfd);
	return EX_OK;
}
