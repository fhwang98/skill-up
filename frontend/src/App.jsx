import { Outlet } from "react-router-dom";
import "@/assets/css/index.css";
import Header from "@/components/Header";

function App() {
	return (
		<>
			<Header />
			<main>
				<Outlet />
			</main>
			<footer></footer>
		</>
	);
}

export default App;
