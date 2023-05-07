import react from "@vitejs/plugin-react-swc";
import { defineConfig } from "vite";
import vitePluginFaviconsInject from "vite-plugin-favicons-inject";
import svgr from "vite-plugin-svgr";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), svgr(), vitePluginFaviconsInject("./public/favicon.png")],
});
