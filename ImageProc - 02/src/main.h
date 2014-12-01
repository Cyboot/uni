/*
 * tasks.h
 *
 *  Created on: 03.11.2014
 *      Author: Tim
 */

#ifndef MAIN_H_
#define MAIN_H_

#include "../lib/CMatrix.h"
#include "../lib/CFilter.h"
#include <stdlib.h>
#include <math.h>

using namespace std;

void mainDerive();
void mainDenoise();
void mainLucasKanade();
void mainHornSchunk();
int mainSolution();

CMatrix<float> normalize(CMatrix<float> img);

/** Neumann Boundary Condition in X */
int indexX(CMatrix<float> img, int indexX);

/** Neumann Boundary Condition in Y */
int indexY(CMatrix<float> img, int indexY);

int getIndex(CMatrix<float> img, int x, int y);

CMatrix<float> deriveX(CMatrix<float> img0);
CMatrix<float> deriveY(CMatrix<float> img0);
CMatrix<float> deriveT(CMatrix<float> img0, CMatrix<float> img1);

float gaus(float x, float sigma);

#endif /* MAIN_H_ */
