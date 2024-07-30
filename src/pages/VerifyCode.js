import React, { useState } from "react";
// import { API_DUMMY } from "../../../utils/api";
import axios from "axios";
import Swal from "sweetalert2";
// import {  useNavigate } from "react-router-dom";
import verified from "../components/asset/verified.png";

function VerifyCode() {
  const [email, setEmail] = useState("");
  const [code, setReset_code] = useState("");
  // const navigate = useNavigate();
  const [show, setShow] = useState(false);

  // const verify_code = async (e) => {
  //   e.preventDefault();
  //   let url_hit = `${API_DUMMY}/api/verify-code`;
  //   try {
  //     const response = await axios.post(url_hit, {
  //       email,
  //       code,
  //     });
  //     if (response.status == 200) {
  //       navigate("/reset-password/" + code);
  //       setShow(false);
  //       Swal.fire({
  //         icon: "success",
  //         title: "Kode Verify Berhasil",
  //         showConfirmButton: false,
  //         timer: 1500,
  //       });
  //     }
  //   } catch (error) {
  //     console.log(error);
  //     setShow(false);
  //     Swal.fire({
  //       icon: "error",
  //       title: "Kode verify telah kadarluwarsa",
  //       showConfirmButton: false,
  //       timer: 1500,
  //     });
  //   }
  // };

  return (
    <body class="font-mono ">
      {/* <!-- Container --> */}
      <div class="container mx-auto">
        <br />
        <br />
        <div class="flex justify-center px-6 my-12">
          {/* <!-- Row --> */}
          <div class="w-full xl:w-3/4 lg:w-11/12 flex">
            {/* <!-- Col --> */}
            {/* <div class="hidden lg:block rounded-l-lg"> */}
            <img style={{ width: "50%" }} className="hidden lg:block rounded-l-lg" src={verified} alt="" />
            {/* </div> */}
            {/* <!-- Col --> */}
            <div class="w-full lg:w-1/2 bg-white h-fit mt-3 p-5 rounded-lg lg:rounded-l-none shadow-lg shadow-slate-400">
              <div class="px-8 mb-4 text-center">
                <h3 class="pt-4 mb-2 text-2xl">Verify Kode</h3>
                <p class="mb-4 text-sm text-gray-700">
                  Masukan Kode yang di dapat dari gmail dengan benar
                </p>
              </div>
              <form
                // onSubmit={verify_code}
                class="px-8 pt-6 h-fit bg-white rounded">
                <div class="mb-4">
                  <label
                    class="block mb-2 text-sm font-bold text-gray-700"
                    for="email">
                    Email
                  </label>
                  <input
                    class="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                    id="email"
                    type="email"
                    placeholder="Masukan alamat email..."
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div class="mb-4">
                  <label
                    class="block mb-2 text-sm font-bold text-gray-700"
                    for="email">
                    Kode
                  </label>
                  <input
                    class="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                    id="kode"
                    type="string"
                    placeholder="Masukan verify kode..."
                    required
                    value={code}
                    onChange={(e) => setReset_code(e.target.value)}
                  />
                </div>
                <div class="mb-6 text-center">
                  <button
                    class="w-full px-4 py-2 font-bold text-white bg-indigo-500 rounded-full hover:bg-indigo-700 focus:outline-none focus:shadow-outline"
                    type="submit">
                    Submit
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </body>
  );
}

export default VerifyCode;
