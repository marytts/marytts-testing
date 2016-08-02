import groovy.util.logging.Log4j

import marytts.LocalMaryInterface
import marytts.Version
import marytts.util.data.audio.MaryAudioUtils

import org.apache.commons.lang.exception.ExceptionUtils

@Log4j
class Text2Audio {
    static void main(args) {
        def (localeStr, textDirPath, wavDirPath) = args.take(3)

        // init mary
        def locale = Locale.forLanguageTag(localeStr == 'en' ? 'en-US' : localeStr)
        def mary = new LocalMaryInterface()
        mary.locale = locale
        println "MaryTTS ${Version.specificationVersion()}, ${mary.locale.displayLanguage}"

        // init input, output directories, parser
        def textDir = new File(textDirPath)
        def wavDir = new File(wavDirPath)
        def parser = new XmlSlurper(false, false)

        // process
        textDir.eachFile { inputFile ->
            println "Synthesizing $inputFile.name"
            def outputFile = new File(wavDir, inputFile.name - 'txt' + 'wav')
            try {
                def audio = mary.generateAudio(inputFile.text)
                MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), outputFile.path, audio.format);
            } catch (Exception e) {
                log.error ExceptionUtils.getStackTrace(e)
            }
            println "Wrote $outputFile.name"
        }

    }
}
