import {configureStore, createSlice} from "@reduxjs/toolkit"

const saveusertolocalstorage = (user) => {
  if (typeof window !== "undefined" && localStorage) {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user"); // clean invalid
    }
  }
}


const initialState = {
    user:null
};

const userSlice = createSlice({
    name:"user",
    initialState,
    reducers:{
        setUser:(state, action)=>{
            state.user=action.payload
            saveusertolocalstorage(action.payload)
        },
        clearUser:(state)=>{
            state.user=null
            localStorage.removeItem("user")
        }
    }
})

export const {clearUser, setUser} = userSlice.actions;
const store = configureStore({
    reducer:{
        user:userSlice.reducer,
    }
})

export default store