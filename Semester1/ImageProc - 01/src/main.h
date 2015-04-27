/*
 * tasks.h
 *
 *  Created on: 03.11.2014
 *      Author: Tim
 */

#ifndef MAIN_H_
#define MAIN_H_

#include "../lib/CMatrix.h"
#include <stdlib.h>
#include <math.h>

int mainNoise();

int mainPSNR();

int mainFilterBox();

int mainFilterGaus();

int mainFilterRecursive();

int main50Img();

CMatrix<float> addGausNoise(CMatrix<float> matrix, int factor);

float computePSNR(CMatrix<float> origin, CMatrix<float> noise);

#endif /* MAIN_H_ */
