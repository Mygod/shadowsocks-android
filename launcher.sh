#!/bin/bash
convert shadow.png -resize 48x48 core/src/main/res/drawable-mdpi/ic_launcher.png
convert shadow.png -resize 72x72 core/src/main/res/drawable-hdpi/ic_launcher.png
convert shadow.png -resize 96x96 core/src/main/res/drawable-xhdpi/ic_launcher.png
convert shadow.png -resize 144x144 core/src/main/res/drawable-xxhdpi/ic_launcher.png
