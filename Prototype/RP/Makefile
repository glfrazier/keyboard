SOURCE := mcp3008Spi.cpp spitest.cpp

INCLUDES := mcp3008Spi.h

CXXFLAGS = -I.

all: spitest

spitest: $(SOURCE) $(INCLUDES)
	g++ $(CXXFLAGS) $(SOURCE) -o spitest
