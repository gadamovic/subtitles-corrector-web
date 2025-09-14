         
         
export function createSubtitleFileName(filename, targetFormat, sourceFormat) {

if (filename.toLowerCase().endsWith("." + sourceFormat.toLowerCase())) {
    const dotIndex = filename.lastIndexOf('.');
    filename = filename.substr(0, dotIndex);
    filename += ("." + targetFormat.toLowerCase());
} else {
    filename += ("." + targetFormat.toLowerCase());
}

return filename;
}


export function createDownloadedFileName(filename, targetFormat, sourceFormat) {
return '[subtitles-corrector.com]-' + createSubtitleFileName(filename, targetFormat, sourceFormat);
}