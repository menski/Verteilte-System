#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <sysexits.h>
#include <err.h>

#define PORT 10041
#define BUF_SIZE 1024
#define MATNR_MIN 1
#define MATNR_MAX 999999

enum { ERROR = -1, SUCCESS };

int main(int argc, char *argv[]) {
	int client_sockfd, port, result;
	int msg_id, mat_nr, rand_nr;
	struct sockaddr_in server_address;
	char *addr_string, buf[BUF_SIZE];
	
	/* parse arguments */
	if (argc < 3) {
		errx(EX_USAGE, "Server address and Mat-NR. required\nusage: tcp_client SERVER_ADDR MAT_NR [PORT]");
	}
	addr_string = argv[1];
	mat_nr = atoi(argv[2]);
	if (mat_nr < MATNR_MIN || mat_nr > MATNR_MAX) {
		errx(EX_USAGE, "MAT_NR must be between %d and %d\nusage: tcp_client SERVER_ADDR MAT_NR [PORT]", MATNR_MIN, MATNR_MAX);
	}
	port = argc > 3 ? atoi(argv[3]) : PORT;


	/* create client socket */
	client_sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if (client_sockfd == ERROR) {
		err(EX_OSERR, "unable to create socket");
	}

	/* set server address values; */
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr= inet_addr(addr_string);
	server_address.sin_port = port;

	/* connect to server */
	result = connect(client_sockfd, (struct sockaddr *) &server_address, sizeof(server_address));
	if (result == ERROR) {
		close(client_sockfd);
		err(EX_OSERR, "unable to connect to address %s:%d", addr_string, port);
	}

	/* send first message to server */
	result = sprintf(buf, "%d %06d", 1, mat_nr);
	if (result < 0) {
		close(client_sockfd);
		errx(EX_DATAERR, "unable to format message");
	}
	result = write(client_sockfd, buf, result + 1); /* +1 for \0 string terminator */
	if (result == ERROR) {
		close(client_sockfd);
		err(EX_OSERR, "unable to send message");
	}

	/* read answer from server */
	result = read(client_sockfd, buf, BUF_SIZE);
	if (result == ERROR) {
		close(client_sockfd);
		err(EX_OSERR, "unable to read message from server");
	}
	printf("%s: received \"%s\"\n", argv[0], buf);
	result = sscanf(buf, "%d %d %d", &msg_id, &mat_nr, &rand_nr);
	if (result != 3) {
		close(client_sockfd);
		errx(EX_DATAERR, "parsing error");
	}
	if (msg_id != 2) {
		close(client_sockfd);
		errx(EX_DATAERR, "wrong message id from client");
	}

	/* send second message to server */
	result = sprintf(buf, "%d %06d %07d", 3, mat_nr, rand_nr);
	if (result < 0) {
		close(client_sockfd);
		errx(EX_DATAERR, "unable to format message");
	}
	result = write(client_sockfd, buf, result + 1); /* +1 for \0 string terminator */
	if (result == ERROR) {
		close(client_sockfd);
		err(EX_OSERR, "unable to send message");
	}

	/* close socket and exit */
	close(client_sockfd);
	return EX_OK;
}
