import java.awt.Font
import java.io.File

class Fonts {
    companion object {
        val TITLE: Font = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/SoyuzGroteskBold/Soyuz Grotesk Bold.otf"""))
        val TITLE_ALT: Font = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/Mont/Mont-Thin.ttf"""))
        val REGULAR: Font = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/Comfortaa/static/Comfortaa-Bold.ttf"""))
        val REGULAR_ALT: Font = Font.createFont(Font.TRUETYPE_FONT, File("""resources/fonts/Montserrat/static/Montserrat-Medium.ttf"""))

    }

    // https://typefaces.temporarystate.net/preview/SoyuzGrotesk
    // https://fonts-online.ru/fonts/mont
    // https://blogfonts.com/aqum-two.font
}
