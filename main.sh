#!/bin/bash
ant && java -Xms2g -Xmx4g -cp bin:lib/* frangel.Main -results-folder="./" -time=1800 "$@"

