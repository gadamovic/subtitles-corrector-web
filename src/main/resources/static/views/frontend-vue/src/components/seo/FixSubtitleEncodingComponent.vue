<template>
    <div class="columns is-centered m-5">
        <div class="box has-text-grey column is-two-thirds">
            <div class="content p-5">

                <h1>Fix Subtitle Encoding Issues (SRT, UTF-8, Broken Characters)</h1>

                <p>
                If your subtitles look like this:
                </p>

                <ul>
                <li>Život je lep → Å½ivot je lep</li>
                <li>Č → È</li>
                <li>Ć → Æ</li>
                <li>Snimićemo film. Obećavam. → Snimiæemo film. Obeæavam.</li>
                </ul>

                <p>This type of encoding problems are particularly common among Slavic / Balkan languages.</p>

                <p>
                You're dealing with a <strong>subtitle encoding issue</strong>. This usually happens when subtitle files are saved in the wrong character format (like ANSI instead of UTF-8).
                </p>

                <h2>How to Fix Subtitle Encoding Issues</h2>

                <p>
                The easiest way is to use an online tool that automatically detects and fixes encoding problems.
                </p>

                <strong>Upload your subtitle file <a href="/">here</a> to fix encoding instantly.</strong>

                <h2>What Causes Broken Subtitle Characters?</h2>

                <ul>
                <li>Incorrect encoding (ANSI, ISO-8859-1 instead of UTF-8)</li>
                <li>Subtitles downloaded from different regions</li>
                <li>Media players misinterpreting character sets</li>
                </ul>

                <h2>Manual Fix (Alternative Method)</h2>

                <p>
                If you prefer to fix this manually:
                </p>

                <ol>
                <li>Open the subtitle file in a text editor (like Notepad++)</li>
                <li>Change encoding to UTF-8</li>
                <li>Sometimes you need to replace manually every occurence of invalid character with the correct one</li>
                <li>Save the file</li>
                </ol>

                <p>
                However, this can be time-consuming.
                </p>

                <h2>Why Use This Tool?</h2>

                <ul>
                <li>Automatically detects encoding issues</li>
                <li>Converts subtitles to UTF-8</li>
                <li>Fixes broken characters instantly</li>
                <li>No installation required</li>
                </ul>

                <h2>Watch live as your subtitle is being fixed and see what changes were applied</h2>

                <p>
                You can track exactly what is happening to your subtitle 
                <strong>live</strong> as it's being processed on our server
                </p>

                <div class="image-stack">
                <img src="@/assets/subtitles-seo/changes-live.png" alt="Live subtitle changes preview" />
                <img src="@/assets/subtitles-seo/changes-diff.png" alt="Subtitle diff changes preview" />
                </div>

                <h2>FAQ</h2>

                <p><strong>What encoding should subtitles use?</strong><br>
                UTF-8 is the most widely supported and recommended encoding format.</p>

                <p><strong>Will this fix all languages?</strong><br>
                Yes, UTF-8 supports a wide range of languages including special characters.</p>

            </div>
        </div>
    </div>
</template>

<script>

</script>