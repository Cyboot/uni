#include "precisetimer.h"
#include <windows.h>

int PreciseTimer::m_frequency = 0;
bool PreciseTimer::s_bTiming = false;

std::map<std::string, TimeMeasurement> PreciseTimer::m_counters;

 struct TimeMeasurement
{
  LARGE_INTEGER startValue;
  long long currentDelta;
  int count;
  long long accumDelta;
};

void  PreciseTimer::start(const std::string& identifier)
{
  LARGE_INTEGER counterValue;
  QueryPerformanceCounter(&counterValue);
  if (m_counters.find(identifier) == m_counters.end())
  {
    m_counters[identifier].accumDelta = 0;
    m_counters[identifier].count = 0;
  }
  m_counters[identifier].startValue = counterValue;
}



double  PreciseTimer::stop(const std::string& identifier)
{
	LARGE_INTEGER counterValue;
  QueryPerformanceCounter(&counterValue);
  TimeMeasurement& tm = m_counters[identifier];
  tm.currentDelta = (long long)(counterValue.QuadPart - tm.startValue.QuadPart);
  
 /* if (avgMode == AverageMode::AVG2 || avgMode == AverageMode::AVG3 || avgMode == AverageMode::AVG10)
  {
    int r = rand();
    if (r % (int)avgMode == 0)
    {
      tm.accumDelta += tm.currentDelta;
      tm.count ++;
    }
  }
  else
  {*/
  if (tm.count > MAX_COUNT)
  {
    tm.accumDelta = 0;
    tm.count = 0;
  }
    tm.accumDelta += tm.currentDelta;
    tm.count ++;
  //}
 
  if (m_frequency == 0)
  {
    LARGE_INTEGER li;
    QueryPerformanceFrequency(&li);
    m_frequency = (int)li.QuadPart;
  }

  return ((double)tm.currentDelta) * (1000.0 / (double)m_frequency);
}


double  PreciseTimer::getAccum(const std::string& identifier)
{
  if (m_counters.find(identifier) != m_counters.end())
  {
    TimeMeasurement& tm = m_counters[identifier];
    long long avgValue = tm.accumDelta / tm.count;

    return (double)avgValue * 1000.0 / (double)m_frequency;
  }
  else return 0;
}

double PreciseTimer::lastMeasurement(const std::string& identifier)
{
  if (m_counters.find(identifier) != m_counters.end())
  {
    TimeMeasurement& tm = m_counters[identifier];
    return (double)tm.currentDelta * 1000.0 / (double)m_frequency;
  }
  else return 0;
}
