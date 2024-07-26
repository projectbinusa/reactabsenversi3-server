import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import Swal from "sweetalert2";
import { useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { API_DUMMY } from "../../../utils/api";

function AddShift() {
  const [waktuMasuk, setWaktuMasuk] = useState("");
  const [waktuPulang, setWaktuPulang] = useState("");
  const [namaShift, setNamaShift] = useState("");
  const [idAdmin, setIdAdmin] = useState("");
  const [adminList, setadminList] = useState([]);
  const token = localStorage.getItem("token");
  const history = useHistory();

  const getAllAdmin = async () => {
    try {
      const response = await axios.get(`${API_DUMMY}/api/admin/all`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setadminList(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    getAllAdmin();
  }, []);

  const TambahShift = async (e) => {
    e.preventDefault();
    const shift = {
      namaShift: namaShift,
      waktuMasuk: waktuMasuk,
      waktuPulang: waktuPulang,
      adminId: idAdmin,
    };
    try {
      const response = await axios.post(
        `${API_DUMMY}/api/shift/tambahShift/${idAdmin}`,
        shift
      );
      Swal.fire({
        title: "Berhasil",
        text: "Berhasil menambahkan shift",
        icon: "success",
        timer: 2000,
        showConfirmButton: false,
      });
      history.push("/superadmin/shift");
    } catch (error) {
      Swal.fire({
        title: "Error",
        text: "Gagal menambahkan shift",
        icon: "error",
        timer: 2000,
        showConfirmButton: false,
      });
      console.log(error);
    }
  };

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className=" sm:ml-64 content-page container p-8  ml-14 md:ml-64 mt-12">
          <div className="p-4">
            <div className="p-5 ">
              {/* <!-- Card --> */}
              <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
                {/* <!-- Header --> */}
                <div className="flex justify-between">
                  <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                    Tambah Shift
                  </h6>
                </div>

                <hr />

                <div className="mt-5 text-left">
                  {/* <!-- Form Input --> */}
                  <form onSubmit={TambahShift}>
                    <div className="grid md:grid-cols-2 md:gap-6">
                      {/* <!-- Shift Input --> */}
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="text"
                          name="name"
                          id="nama_shift"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={namaShift}
                          onChange={(e) => setNamaShift(e.target.value)}
                        />
                        <label
                          htmlFor="nama_shift"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Nama Shift
                        </label>
                      </div>

                      {/* <!-- Jam Masuk Input --> */}
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="time"
                          name="time_masuk"
                          id="jam_masuk"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={waktuMasuk}
                          onChange={(e) => setWaktuMasuk(e.target.value)}
                        />
                        <label
                          htmlFor="jam_masuk"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Masuk
                        </label>
                      </div>
                    </div>

                    <div className="grid md:grid-cols-2 md:gap-6">
                      {/* <!-- Jam Pulang Input --> */}
                      <div className="relative z-0 w-full mb-6 group">
                        <input
                          type="time"
                          name="time_pulang"
                          id="jam_pulang"
                          className="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                          placeholder=" "
                          autoComplete="off"
                          required
                          value={waktuPulang}
                          onChange={(e) => setWaktuPulang(e.target.value)}
                        />
                        <label
                          htmlFor="jam_pulang"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Jam Pulang
                        </label>
                      </div>
                      <div className="relative z-0 w-full mb-6 group">
                        <select
                          value={idAdmin}
                          onChange={(e) => setIdAdmin(e.target.value)}
                          name="id_admin"
                          className="block py-2.5 px-0 w-full text-sm text-gray-500 bg-transparent border-0 border-b-2 border-gray-200 appearance-none dark:text-gray-400 dark:border-gray-700 focus:outline-none focus:ring-0 focus:border-gray-200 peer"
                        >
                          <option value="" disabled >
                            Pilih Admin
                          </option>
                          {adminList &&
                            adminList.map((org) => (
                              <option key={org.id} value={org.id}>
                                {org.username}
                              </option>
                            ))}
                        </select>
                        <label
                          htmlFor="Admin"
                          className="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                        >
                          Admin
                        </label>
                      </div>
                    </div>

                    {/* <!-- Button --> */}
                    <div className="flex justify-between">
                      <a
                        className="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                        href="/superadmin/shift"
                      >
                        <FontAwesomeIcon icon={faArrowLeft} />
                      </a>
                      <button
                        type="submit"
                        className="text-white bg-indigo-500 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800"
                      >
                        <FontAwesomeIcon icon={faFloppyDisk} />
                      </button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AddShift;
