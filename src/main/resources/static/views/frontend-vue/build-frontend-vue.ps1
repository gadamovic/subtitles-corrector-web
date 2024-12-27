echo "Building frontend files..."
npm run build --no-cache
echo "Copying content from /dist folder to tomcat's webroot folder..."
Copy-item -Path ./dist/* -Destination ../ -Recurse -Force