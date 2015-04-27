#include "main.h"
#include <sys/time.h>

int main(int argc, char** args) {
	clock_t t1 = clock();

	//DO STUFF

	clock_t t2 = clock();

	long millis = (t2 - t1) * (1000.0 / CLOCKS_PER_SEC);
	cout << "took " << millis << " ms" << endl;

	return 0;
}
