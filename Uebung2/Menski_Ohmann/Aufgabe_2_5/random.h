/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _RANDOM_H_RPCGEN
#define _RANDOM_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif


struct messagecontent {
	int messagetype;
	int matrikelnumber;
	int randomvalue;
};
typedef struct messagecontent messagecontent;

#define RANDOM_PROG 0x23452222
#define RANDOM_VERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define GETRANDOMVALUE 1
extern  int * getrandomvalue_1(messagecontent *, CLIENT *);
extern  int * getrandomvalue_1_svc(messagecontent *, struct svc_req *);
#define SETMANIPULATEDVALUE 2
extern  int * setmanipulatedvalue_1(messagecontent *, CLIENT *);
extern  int * setmanipulatedvalue_1_svc(messagecontent *, struct svc_req *);
extern int random_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define GETRANDOMVALUE 1
extern  int * getrandomvalue_1();
extern  int * getrandomvalue_1_svc();
#define SETMANIPULATEDVALUE 2
extern  int * setmanipulatedvalue_1();
extern  int * setmanipulatedvalue_1_svc();
extern int random_prog_1_freeresult ();
#endif /* K&R C */

/* the xdr functions */

#if defined(__STDC__) || defined(__cplusplus)
extern  bool_t xdr_messagecontent (XDR *, messagecontent*);

#else /* K&R C */
extern bool_t xdr_messagecontent ();

#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_RANDOM_H_RPCGEN */