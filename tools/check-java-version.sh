#!/bin/bash

for jarFile in *.jar; do
    echo "Checking $jarFile..."
    
    # Extract the MANIFEST.MF file from the JAR
    manifestFile=$(unzip -l "$jarFile" | grep "META-INF/MANIFEST.MF" | awk '{print $4}')
    unzip -p "$jarFile" "$manifestFile" > temp_manifest.mf

    requireCapability=$(grep "Require-Capability" temp_manifest.mf)

    if [ -n "$requireCapability" ]; then
        echo "Require-Capability for $jarFile:"
        echo "$requireCapability"
    else
        echo "Require-Capability not found in the manifest of $jarFile"
    fi
    
    # Clean up temporary files
    rm temp_manifest.mf
done

