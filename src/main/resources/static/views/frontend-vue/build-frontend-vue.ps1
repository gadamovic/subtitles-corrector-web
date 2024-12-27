echo "Deleting current content from /js and /css folders..."
rm -r ../js/*
rm -r ../css/*
echo "Building frontend files..."
npm run build --no-cache
echo "Copying content from /dist folder to tomcat's webroot folder..."
Copy-item -Path ./dist/* -Destination ../ -Recurse -Force