import React, { useState } from "react";
import { API_DUMMY } from "../../utils/api";
import axios from "axios";
import Swal from "sweetalert2";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { Link, useParams } from "react-router-dom";
import resetPass from "../../components/asset/resetPassword.png";

function ResetPasswordSup() {
  const [new_password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [konfirmPassword, setKonfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const token = useParams();

  const reset_password = async (e) => {
    e.preventDefault();
    if (new_password !== konfirmPassword) {
      Swal.fire({
        icon: "error",
        title: "Password dan Konfirmasi Password tidak cocok",
        showConfirmButton: false,
        timer: 1500,
      });
      return;
    }

    let url_hit = `${API_DUMMY}/api/superadmin/ubahPassByForgot`;
    try {
      const response = await axios.put(url_hit, {
        email: email,
        new_password: new_password,
        confirm_new_password: konfirmPassword,
      });
      if (response.status === 200) {
        Swal.fire({
          icon: "success",
          title: "Password berhasil di reset",
          showConfirmButton: false,
          timer: 1500,
        });
        window.location.href = "/";
      }
    } catch (error) {
      if (error.response && error.response.status === 400) {
        Swal.fire("Gagal", error.response.data.message, "error");
      } else {
        console.log(error);
        Swal.fire("Gagal", "Terjadi kesalahan pada server", "error");
      }
    }
  };

  return (
    <>
      <div className="font-mono container mb-2">
        <br />
        <br />
        <div className="flex justify-center">
          <div className="w-full xl:w-3/4 lg:w-11/12 flex justify-center gap-10">
            <img
              style={{ width: "50%" }}
              className="hidden lg:block rounded-l-lg"
              src={resetPass}
              alt="Reset Password"
            />
            <div className="w-full lg:w-1/2 bg-white p-5 rounded-lg lg:rounded-l-none shadow-lg shadow-slate-400">
              <div className="px-8 mb-4 text-center">
                <h3 className="pt-4 mb-2 text-2xl">Reset Password</h3>
                <p className="mb-4 text-sm text-gray-700">
                  Ganti password baru anda di sini
                </p>
              </div>
              <form
                onSubmit={reset_password}
                className="px-8 pt-6 pb-8 bg-white rounded"
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
                    placeholder="Masukan email anda"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <div className="mb-4 md:mr-2 md:mb-0 relative">
                    <label
                      className="block mb-2 text-sm font-bold text-gray-700"
                      htmlFor="password"
                    >
                      Password
                    </label>
                    <input
                      className="w-full px-3 py-2 mb-3 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                      id="password"
                      type={showPassword ? "text" : "password"}
                      placeholder="*********"
                      value={new_password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                    <span
                      className="absolute inset-y-0 right-0 top-10 pr-3 cursor-pointer"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? <FaEye /> : <FaEyeSlash />}
                    </span>
                    <p className="text-xs italic text-blue-500">
                      Kata sandi 8 digit huruf
                    </p>
                    <p className="text-xs italic text-blue-500 mb-3">
                      besar & kecil
                    </p>
                  </div>
                  <div className="mb-4 md:mr-2 md:mb-0 relative">
                    <label
                      className="block mb-2 text-sm font-bold text-gray-700"
                      htmlFor="konfirmPassword"
                    >
                      Konfirm Password
                    </label>
                    <input
                      className="w-full px-3 py-2 text-sm leading-tight text-gray-700 border rounded shadow appearance-none focus:outline-none focus:shadow-outline"
                      id="konfirmPassword"
                      type={showConfirmPassword ? "text" : "password"}
                      placeholder="*********"
                      required
                      value={konfirmPassword}
                      onChange={(e) => setKonfirmPassword(e.target.value)}
                    />
                    <span
                      className="absolute inset-y-0 right-0 pr-3 top-10 cursor-pointer"
                      onClick={() =>
                        setShowConfirmPassword(!showConfirmPassword)
                      }
                    >
                      {showConfirmPassword ? <FaEye /> : <FaEyeSlash />}
                    </span>
                  </div>
                </div>
                <div className="mb-6 text-center">
                  <button
                    className="w-full px-4 py-2 font-bold text-white bg-indigo-500 rounded-full hover:bg-indigo-700 focus:outline-none focus:shadow-outline"
                    type="submit"
                  >
                    Reset Password
                  </button>
                </div>
                <hr className="mb-6 border-t" />
                <div className="text-center">
                  <Link
                    className="inline-block text-sm w-full text-blue-500 align-baseline hover:text-blue-800"
                    to="/forgotpass"
                  >
                    Code Kadarluwarsa? Kirim ulang email anda di forgot password
                  </Link>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default ResetPasswordSup;
