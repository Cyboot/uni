#ifdef _MSC_VER

#include <windows.h>
#include <stdio.h>

#include "Textoutput.h"

char Textout::text[MAX_BUF_SIZE];
char Textout::eraseText[MAX_BUF_SIZE];
HANDLE  Textout::consoleHandle = 0;
int Textout::curLine = 0;
int Textout::bufferwidth = 0;
std::map<std::pair<int, int>, int> Textout::busyStates;

void Textout::print(int line, int column, int size, char *s, bool nolinebreak)
{
#ifndef NOOUTPUT
  COORD coords;
  DWORD unused;

  coords.X = column;
  coords.Y = curLine + line;

  if (!consoleHandle) return;

#ifdef SCREENPOSITIONING
  SetConsoleCursorPosition(consoleHandle, coords);
  if ((int)strlen(s) < size)
  {
    WriteConsole(consoleHandle, eraseText, column + size > bufferwidth ? bufferwidth - column : size, &unused, 0);
    //WriteConsole(consoleHandle, eraseText, 1, &unused, 0);
  }
  SetConsoleCursorPosition(consoleHandle, coords);
  WriteConsole(consoleHandle, s, (int)strlen(s), &unused, 0);
#elif defined(FLATTEN)
  printf(s);
  if (!nolinebreak)
  {
    printf("\n");
  }
#endif
#endif
  /*coords.X = 0;
  coords.Y = curLine;
  SetConsoleCursorPosition(consoleHandle, coords);*/
}

void Textout::print(int line, int column, char *s)
{

  print(line, column, bufferwidth, s, false);
}

void Textout::printvar(int line, int column, int size, char *format, ...)
{
  va_list args;
  va_start( args, format);

  vsprintf(text, format, args);
  va_end(args);
  print(line, column, size, text, false);
}

void Textout::printvar(int line, int column, char *format, ...)
{
  va_list args;
  va_start( args, format);

  vsprintf(text, format, args);
  va_end(args);

  print(line, column, bufferwidth, text, false);
}

void Textout::pagebreak()
{
#ifndef NOOUTPUT
  CONSOLE_SCREEN_BUFFER_INFO  sbi;
  CHAR_INFO chiFill;
  COORD coordDest;

  if (!consoleHandle) return;


  GetConsoleScreenBufferInfo(consoleHandle, &sbi);
  int height = sbi.srWindow.Bottom - sbi.srWindow.Top;


  int numpagelines = (int)(sbi.dwSize.Y / height) * height;

  if (curLine == numpagelines - height) {
    SMALL_RECT scrollRect;

    scrollRect.Top = height;
    scrollRect.Bottom = numpagelines;
    scrollRect.Left = 0;
    scrollRect.Right = sbi.dwSize.X;
    chiFill.Char.AsciiChar = ' '; 
    chiFill.Attributes = 0;

    coordDest.X = 0; 
    coordDest.Y = 0;

    ScrollConsoleScreenBuffer( 
      consoleHandle,         // screen buffer handle 
      &scrollRect, // scrolling rectangle 
      0,   // clipping rectangle 
      coordDest,       // top left destination cell 
      &chiFill);       // fill character and color

  }
  else
  {
    curLine += height;
  }
  /*sbi.srWindow.Top = curLine;
  sbi.srWindow.Bottom = curLine + height;
  SetConsoleWindowInfo(consoleHandle, TRUE, &sbi.srWindow);*/
#endif
}

void Textout::printbreak(int line)
{
  for (int i = 0; i < bufferwidth; i ++)
  {
    text[i] = '-';
  }
  printvar(line, 0, "%s", text);
}

void Textout::printbusy(int line, int column)
{
#ifdef SCREENPOSITIONING
  switch (busyStates[std::pair<int,int>(line, column)])
  {
  case 0:
    print(line, column, 1, "/");
    busyStates[std::pair<int,int>(line, column)] = 1;
    break;
  case 1:
    print(line, column, 1, "-");
    busyStates[std::pair<int,int>(line, column)] = 2;
    break;
  case 2:
    print(line, column, 1, "\\");
    busyStates[std::pair<int,int>(line, column)] = 3;
    break;
  case 3:
    print(line, column, 1, "|");
    busyStates[std::pair<int,int>(line, column)] = 0;
    break;
  default:
    busyStates[std::pair<int,int>(line, column)] = 0;
    break;
  }
#endif
}

void Textout::init()
{
#ifndef NOOUTPUT
  if (!consoleHandle)
  {
    HWND foregroundWindow = GetForegroundWindow();
    AllocConsole();
    consoleHandle = GetStdHandle(STD_OUTPUT_HANDLE);
    BringWindowToTop(foregroundWindow);
  }
  for (int i = 0; i < MAX_BUF_SIZE - 1; i ++) {
    eraseText[i] = ' ';
  }
  eraseText[MAX_BUF_SIZE - 1] = 0;
  CONSOLE_SCREEN_BUFFER_INFO  sbi;
  GetConsoleScreenBufferInfo(consoleHandle, &sbi);
  bufferwidth = sbi.dwSize.X;
#endif
}

  //clear the console
void Textout::clear()
{
#ifndef NOOUTPUT
    CONSOLE_SCREEN_BUFFER_INFO* strConsoleInfo = new CONSOLE_SCREEN_BUFFER_INFO();			
    consoleHandle = GetStdHandle(STD_OUTPUT_HANDLE);

    if (!consoleHandle) return;

    COORD Home;		
    Home.X = Home.Y = 0;
    DWORD writtenchars = 0; 
    GetConsoleScreenBufferInfo(consoleHandle, strConsoleInfo);
    FillConsoleOutputCharacter(consoleHandle,' ', strConsoleInfo->dwSize.X * strConsoleInfo->dwSize.Y, Home,&writtenchars);
    SetConsoleCursorPosition(consoleHandle, Home);
#endif
} 

#endif