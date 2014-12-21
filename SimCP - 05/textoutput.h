#ifndef __TEXTOUTPUT_CPP__
#define __TEXTOUTPUT_CPP__

#include <map>
#include <utility>

const int MAX_BUF_SIZE = 1000;

// windows.h 
typedef void* HANDLE;

#ifndef DEMO_MODE
#define SCREENPOSITIONING
#else
#define NOOUTPUT
#endif
//#define FLATTEN

/** Print formatted text at a certain (x,y) position on the console.
 * To write in the standard mode (i.e. text is not positioned, but every time a new line is inserted), define FLATTEN.
 * To avoid writing, define NOOUTPUT.
 */
class Textout
{
public:
  /** 
   * Prints the string s at position (line,column) in the console.
   * size is the number of characters that are cleared in the console before the string is printed. This prevents that previous outputs
   * that were longer lead to confusing lookings.
   */
  static void print(int line, int column, int size, char *s, bool nolinebreak = false);

  /**
   * @see print(int, int, int, char, bool)
   */
  static void print(int line, int column, char *s);

  /**
   * Prints the formatted string provided with format and a variable number of arguments to the console @see print().
   * The function is similar to printf, and so are the formatting parameters in format. 
   */
  static void printvar(int line, int column, int size, char *format, ...);

  /**
   * Same as printvar(int, int, int, char, ...). 
   * If no size field is provided, then the whole line is cleared before the new string is written.
   */
  static void printvar(int line, int column, char *format, ...);

  /**
   * Inserts a new page in the console. 
   * If the console contains 80 lines, then 80 new lines are inserted and writing continues at position 80 + line.
   */
  static void pagebreak();

  /**
   * Prints a visual line (--------) at position line that spans over the whole width of the console.
   */
  static void printbreak(int line);

  /**
   * Prints a busy cursor at position (line, column).
   *
   * Each time printbusy(line,column) is called for a certain position, the character at this position changes from
   * / -> - -> \ -> | -> ... This provides a 'console busy clock'. 
   */
  static void printbusy(int line, int column);

  /**
   * Clears the whole console window.
   */
  static void clear();

  /**
   * Creates and initializes the Textout-class and its associated console window.
   *
   * This function must be called exactly once on initializing the application.
   */
  static void init();

private:
  static HANDLE  consoleHandle;
  static char    text[MAX_BUF_SIZE];
  static char    eraseText[MAX_BUF_SIZE];
  static int     curLine;
  static int     bufferwidth;
  static std::map<std::pair<int, int>,int> busyStates;
};

#endif
