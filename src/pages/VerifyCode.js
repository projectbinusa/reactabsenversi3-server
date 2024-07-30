import React, { useState, useEffect } from "react";
import { API_DUMMY } from "../utils/api";
import axios from "axios";
import Swal from "sweetalert2";
import verified from "../components/asset/verified.png";

function VerifyCode() {
  const [email, setEmail] = useState("");
  const [code, setReset_code] = useState("");
  const [countdown, setCountdown] = useState(60);
  const [isCountdownActive, setIsCountdownActive] = useState(false);

  const verify_code = async (e) => {
    e.preventDefault();
    let url_hit = `${API_DUMMY}/api/user/validasi-code`;
    try {
      const response = await axios.post(url_hit, {
        email,
        code,
      });
      if (response.status === 200) {
        window.location.href = "/reset-password/" + code;
        Swal.fire({
          icon: "success",
          title: "Kode Verify Berhasil",
          showConfirmButton: false,
          timer: 1500,
        });
      }
    } catch (error) {
      console.log(error);
      Swal.fire({
        icon: "error",
        title: "Kode verify telah kadarluwarsa",
        showConfirmButton: false,
        timer: 1500,
      });
    }
  };

  const send_email = async (e) => {
    e.preventDefault();
    let url_hit = `${API_DUMMY}/api/user/forgot_password`;
    try {
      const response = await axios.post(url_hit, {
        email,
      });
      if (response.status === 200) {
        Swal.fire({
          icon: "success",
          title: "Email pengaturan ulang kata sandi terkirim",
          showConfirmButton: false,
          timer: 1500,
        });
        setIsCountdownActive(true);
        setCountdown(60);
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

  useEffect(() => {
    let timer;
    if (isCountdownActive) {
      timer = setInterval(() => {
        setCountdown((prevCountdown) => {
          if (prevCountdown <= 1) {
            clearInterval(timer);
            setIsCountdownActive(false);
            return 0;
          }
          return prevCountdown - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isCountdownActive]);

  return (
    <body className="font-mono ">
      <div className="container mx-auto">
        <br />
        <br />
        <div className="flex justify-center px-6 my-12">
          <div className="w-full xl:w-3/4 lg:w-11/12 flex">
            <img
              style={{ width: "50%" }}
              className="hidden lg:block rounded-l-lg"
              src={verified}
              alt=""
            />
            <div className="w-full lg:w-1/2 bg-white h-fit mt-3 p-5 rounded-lg lg:rounded-l-none shadow-lg shadow-slate-400">
              <div className="px-8 mb-4 text-center">
                <h3 className="pt-4 mb-2 text-2xl">Verify Kode</h3>
                <p className="mb-4 text-sm text-gray-700">
                  Masukan Kode yang di dapat dari gmail dengan benar
                </p>
              </div>
              <form
                onSubmit={verify_code}
                className="px-8 pt-6 h-fit bg-white rounded"
              >
                <div className="mb-4">
                  <label
                    className="block mb-2 text-sm font-bold text-gray-700"
                    htmlFor="email"
                  >
                    Email
                  </label>
                  <input
                    className="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                    id="email"
                    type="email"
                    placeholder="Masukan alamat email..."
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div className="mb-4">
                  <label
                    className="block mb-2 text-sm font-bold text-gray-700"
                    htmlFor="kode"
                  >
                    Kode
                  </label>
                  <input
                    className="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                    id="kode"
                    type="text"
                    placeholder="Masukan verify kode..."
                    required
                    value={code}
                    onChange={(e) => setReset_code(e.target.value)}
                  />
                </div>
                <div className="mb-6 text-center">
                  <button
                    className="w-full px-4 py-2 font-bold text-white bg-indigo-500 rounded-full hover:bg-indigo-700 focus:outline-none focus:shadow-outline"
                    type="submit"
                  >
                    Submit
                  </button>
                </div>
                <div className="text-center">
                  <button
                    className="inline-block text-sm w-full text-blue-500 align-baseline hover:text-blue-800"
                    onClick={send_email}
                    disabled={isCountdownActive}
                  >
                    {isCountdownActive
                      ? `Kirim ulang email dalam ${countdown} detik`
                      : "Code Kadarluwarsa? Kirim ulang email anda di forgot password"}
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
