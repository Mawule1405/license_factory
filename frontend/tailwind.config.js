/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        // Les couleurs officielles Taurus extraites de tes images
        'taurus-green': '#006D5B', // Vert émeraude du branding
        'taurus-white': '#FFFFFF',
        'taurus-dark': '#004D40', // Une variante plus sombre pour les survols
      },
      fontFamily: {
        // Ton branding utilise une police sans-serif géométrique et moderne
        'taurus': ['Montserrat', 'Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
