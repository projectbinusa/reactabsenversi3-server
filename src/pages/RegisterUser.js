import axios from "axios";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import Swal from "sweetalert2";
import Logo from "../components/absensii.png";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { API_DUMMY } from "../utils/api";

function RegisterUser() {
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [idOrganisasi, setIdOrganisasi] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [organisasiList, setOrganisasiList] = useState([]);
  const [shiftList, setShiftList] = useState([]);
  const [selectedShift, setSelectedShift] = useState("");
  const history = useHistory();
  const [noShiftsError, setNoShiftsError] = useState(false);

  useEffect(() => {
   
    GetALLOrganisasi();
  }, []);

  const GetALLOrganisasi = async () => {
    try {
      const response = await axios.get(`${API_DUMMY}/api/organisasi/all`);
      setOrganisasiList(response.data);
    } catch (error) {
      console.log(error);
    }
  };

  const handleOrganisasiChange = async (e) => {
    const organisasiId = e.target.value;
    setIdOrganisasi(organisasiId);
    const selectedOrganisasi = organisasiList.find(
      (org) => org.id === parseInt(organisasiId)
    );
    if (selectedOrganisasi) {
      await fetchShiftsByAdmin(selectedOrganisasi.admin.id);
    } else {
      console.log("Organisasi tidak ditemukan");
    }
  };

  const fetchShiftsByAdmin = async (idAdmin) => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/shift/getbyadmin?idAdmin=${idAdmin}`
      );
      if (response.data.length === 0) {
        setNoShiftsError(true);
      } else {
        setShiftList(response.data);
        setNoShiftsError(false);
      }
    } catch (error) {
      console.log(error);
      setNoShiftsError(true);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
    if (!passwordRegex.test(password)) {
      Swal.fire({
        position: "center",
        icon: "warning",
        title: "Password tidak sesuai",
        showConfirmButton: false,
        timer: 1500,
      });
      return;
    }

    try {
      await axios.post(
        `${API_DUMMY}/api/user/register?idOrganisasi=${idOrganisasi}&idShift=${selectedShift}`,
        {
          username,
          email,
          password,
        }
      );

      setShowPassword(false);
      Swal.fire({
        icon: "success",
        title: "Berhasil Register",
        showConfirmButton: false,
        timer: 1500,
      });
      history.push("/");
      setTimeout(() => {
        window.location.reload();
      }, 1500);
    } catch (error) {
      console.error("Error during registration:", error);
      Swal.fire("Error", "Gagal mendaftar silahkan coba lagi", "error");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 text-gray-900 flex justify-center">
      <div className="max-w-screen-xl m-0 sm:m-10 bg-white shadow sm:rounded-lg flex justify-center flex-1">
        <div className="lg:w-1/2 xl:w-5/12 p-2">
          <div>
            <img src={Logo} className="w-16 mx-auto" alt="Logo" />
          </div>
          <div className="mt-2 flex flex-col items-center">
            <h1 className="text-2xl xl:text-3xl font-extrabold">Sign up</h1>
            <div className="w-full flex-1 mt-8">
              <form onSubmit={handleSubmit} method="POST">
                <div className="mx-auto max-w-xs">
                  <input
                    className="w-full px-8 py-4 rounded-lg font-medium bg-gray-100 border border-gray-200 placeholder-gray-500 text-sm focus:outline-none focus:border-gray-400 focus:bg-white"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    placeholder="Email"
                  />
                  <input
                    className="w-full px-8 py-4 rounded-lg font-medium bg-gray-100 border border-gray-200 placeholder-gray-500 text-sm focus:outline-none focus:border-gray-400 focus:bg-white mt-5"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    placeholder="Username"
                  />
                  <select
                    className="w-full px-8 py-4 rounded-lg font-medium bg-gray-100 border border-gray-200 placeholder-gray-500 text-sm focus:outline-none focus:border-gray-400 focus:bg-white mt-5"
                    value={idOrganisasi}
                    onChange={handleOrganisasiChange}
                    required
                  >
                    <option value="" disabled>
                      Pilih Organisasi
                    </option>
                    {organisasiList.map((org) => (
                      <option key={org.id} value={org.id}>
                        {org.namaOrganisasi}
                      </option>
                    ))}
                  </select>
                  {noShiftsError ? (
                    <div className="mt-5 text-red-600">
                      Organisasi belum memiliki shift
                    </div>
                  ) : shiftList.length > 0 ? (
                    <select
                      className="w-full px-8 py-4 rounded-lg font-medium bg-gray-100 border border-gray-200 placeholder-gray-500 text-sm focus:outline-none focus:border-gray-400 focus:bg-white mt-5"
                      value={selectedShift}
                      onChange={(e) => setSelectedShift(e.target.value)}
                      required
                    >
                      <option value="" disabled>
                        Pilih Shift
                      </option>
                      {shiftList.map((shift) => (
                        <option key={shift.id} value={shift.id}>
                          {shift.namaShift}
                        </option>
                      ))}
                    </select>
                  ) : null}
                  <div className="relative mt-5">
                    <input
                      className="w-full px-8 py-4 rounded-lg font-medium bg-gray-100 border border-gray-200 placeholder-gray-500 text-sm focus:outline-none focus:border-gray-400 focus:bg-white"
                      placeholder="Password*"
                      type={showPassword ? "text" : "password"}
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                    <span
                      className="absolute inset-y-0 right-0 pr-3 flex items-center cursor-pointer"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? <FaEye /> : <FaEyeSlash />}
                    </span>
                  </div>
                  <button className="mt-5 tracking-wide font-semibold bg-indigo-500 text-gray-100 w-full py-4 rounded-lg hover:bg-indigo-700 transition-all duration-300 ease-in-out flex items-center justify-center focus:shadow-outline focus:outline-none">
                    <svg
                      className="w-6 h-6 -ml-2"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M16 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
                      <circle cx="8.5" cy="7" r="4" />
                      <path d="M20 8v6M23 11h-6" />
                    </svg>
                    <span className="ml-3">Sign Up</span>
                  </button>
                  <p className="mt-6 text-base text-gray-600 text-center">
                    Sudah mempunyai akun?
                    <a
                      href="/"
                      className="border-b border-blue-500 border-dotted"
                    >
                      Login
                    </a>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
        <div className="flex-1 bg-indigo-100 text-center hidden lg:flex">
          <div
            className="m-12 xl:m-16 w-full bg-contain bg-center bg-no-repeat"
            style={{
              backgroundImage:
                "url('https://storage.googleapis.com/devitary-image-host.appspot.com/15848031292911696601-undraw_designer_life_w96d.svg')",
            }}
          ></div>
        </div>
      </div>
    </div>
  );
}

export default RegisterUser;
