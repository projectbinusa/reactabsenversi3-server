import React, { useState } from "react";
import { API_DUMMY } from "../utils/api";
import axios from "axios";
import forgotPass from "../components/asset/Forgot password.png";
import Swal from "sweetalert2";

function ForgotPass() {
  const [email, setEmail] = useState("");

  const send_email = async (e) => {
    e.preventDefault();
    let url_hit = `${API_DUMMY}/api/user/forgot_password`;
    try {
      const response = await axios.post(url_hit, {
        email,
      });
      if (response.status == 200) {
        Swal.fire({
          icon: "success",
          title: "Email pengaturan ulang kata sandi terkirim",
          showConfirmButton: false,
          timer: 1500,
        });
        window.location.href = "/verify-code";
      } else {
        Swal.fire("Gagal", "Email tidak ditemukan", "error");
      }
    } catch (error) {
      if (error.response && error.response.status === 500) {
        Swal.fire("Gagal", "Email tidak ditemukan", "error");
      } else {
        console.log(error);
        Swal.fire("Gagal", "Terjadi kesalahan pada server", "error");
      }
    }
  };

  return (
    <body class="font-mono h-screen bg-gray-100">
      <div class="container mx-auto">
        <br />
        <br />
        <div class="flex justify-center px-6 my-12">
          {/* <!-- Row --> */}
          <div class="w-full xl:w-3/4 lg:w-11/12 flex">
            <img
              style={{ width: "50%" }}
              className="hidden lg:block rounded-l-lg"
              src={forgotPass}
              alt=""
            />

            <div class="w-full lg:w-1/2 bg-white p-5 rounded-lg lg:rounded-l-none shadow-lg shadow-slate-400">
              <div class="px-8 mb-4 text-center">
                <h3 class="pt-4 mb-2 text-2xl">Lupa kata sandi Anda?</h3>
                <p class="mb-4 text-sm text-gray-700">
                  Kami mengerti, banyak hal terjadi. Cukup masukkan alamat email
                  Anda di bawah dan kami akan mengirimkan Anda tautan untuk
                  mengatur ulang kata sandi Anda!
                </p>
              </div>
              <form
                onSubmit={send_email}
                class="px-8 pt-6 pb-8 mb-4 bg-white rounded"
              >
                <div class="mb-4">
                  <label
                    class="block mb-2 text-sm font-bold text-gray-700"
                    for="email"
                  >
                    Email
                  </label>
                  <input
                    class="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                    id="email"
                    type="email"
                    placeholder="Enter Email Address..."
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
                <div class="mb-6 text-center">
                  <button
                    class="w-full px-4 py-2 font-bold text-white bg-indigo-500 rounded-full hover:bg-indigo-700 focus:outline-none focus:shadow-outline"
                    type="submit"
                  >
                    Mengatur Ulang Kata Sandi
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

export default ForgotPass;
