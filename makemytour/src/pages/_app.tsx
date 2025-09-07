import "@/styles/globals.css";
import type { AppProps } from "next/app";
import { Provider } from "react-redux";
import store, { setUser } from "../store"
import Navbar from "@/components/Navbar";
import Head from "next/head";
import { useEffect } from "react";
import Footer from "@/components/Fotter";

const Myapp = ({Component, pageProps} : AppProps) => {
  useEffect(()=>{
    const storedUser = localStorage.getItem("user")
    if(storedUser)
    {
      store.dispatch(setUser(JSON.parse(storedUser)))
    }
  },[]);
  return(
    <div className="min-h-screen">
      <Navbar/>
      <Component {...pageProps} />
      <Footer/>
    </div>
  )
};

export default function App(props: AppProps) {
  return (
  <Provider store={store}> 
    <Head>
      <title>MakeMyTour</title>
    </Head>
    <Myapp {...props} />
  </Provider>
)}