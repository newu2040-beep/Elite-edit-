cmake_minimum_required(VERSION 3.22.1)

project("eliteeditnative")

add_library(eliteeditnative SHARED
    NativeBridge.cpp
    ColorGradingEngine.cpp
    AudioEngine.cpp
    LutEngine.cpp
)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

target_link_libraries(eliteeditnative
    ${log-lib}
    ${jnigraphics-lib}
)
