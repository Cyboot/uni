#include <map>
#include <string>

// forward declaration
struct TimeMeasurement;

class PreciseTimer
{
  enum {
    MAX_COUNT = 500
  };


public:
  static void  start(const std::string& identifier);
  static double  stop(const std::string& identifier);
  static double  getAccum(const std::string& identifier);
  static double lastMeasurement(const std::string& identifier);
  

  static bool s_bTiming;

private:
  static int m_frequency;
  static std::map<std::string,TimeMeasurement> m_counters;
};